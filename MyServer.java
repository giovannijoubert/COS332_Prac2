import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.IOException; 
import java.io.FileWriter; 

public class MyServer {

    public static void createDatabase(){
        try {
            File myObj = new File("database.txt");
            if (myObj.createNewFile()) {
              System.out.println("Database created: " + myObj.getName());
            } else {
              System.out.println("Database already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred whilst creating Database.");
            e.printStackTrace();
          }
    }

    public static void writeToDatabase(String tx){
        try {
            FileWriter myWriter = new FileWriter("database.txt", true);
            myWriter.write(tx + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the Database.");
          } catch (IOException e) {
            System.out.println("An error occurred whilst writing to the Database.");
            e.printStackTrace();
          }
    }

    public static String searchDatabase(String qry){
        try{
            File file=new File("database.txt");    
            FileReader fr=new FileReader(file);  
            BufferedReader br=new BufferedReader(fr);  
            StringBuffer sb=new StringBuffer();    

            String line;  
            while((line=br.readLine())!=null)  
            {  
                String DBEntry = line.substring(line.toString().lastIndexOf(",") + 1).trim().toLowerCase();
                if(qry.toLowerCase().trim().equals(DBEntry)){
                    br.close();  
                    return line;
                }
                
            }  
                fr.close();  
                br.close();  
               

           }  
            catch(IOException e)  
            {  
            e.printStackTrace();  
            }  

            return "no results found";
            
    }

    public static boolean deleteFromDatabase(String lineToRemove){
        File inputFile = new File("database.txt");
        File tempFile = new File("temp.txt");
        Boolean out = false;
        try{
        
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        
        String currentLine;
        
        while((currentLine = reader.readLine()) != null) {
            String trimmedLine = currentLine.trim().substring(0, currentLine.trim().lastIndexOf(","));
            if(trimmedLine.equals(lineToRemove)){
                
                out = true;
                continue;
            } 
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close(); 
        reader.close(); 
        tempFile.renameTo(inputFile);
    }  
        catch(IOException e)  
        {  
        e.printStackTrace();  
        } 

        return out;
    }




    public static void main(String[] args) {
        createDatabase();
        connectToServer();
    }

    public static void connectToServer() {
        
        Random r = new Random();
        int port = r.nextInt(2000-1000)+1 + 1000;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Running on port: " + port);
            
            Socket connectionSocket = serverSocket.accept();
            System.out.println("Client Connected");
            

            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            
            printMenu(serverPrintOut);

            boolean done = false;
            int option = -1;

            while(!done && scanner.hasNextLine()) {
                

                String line = scanner.nextLine();

                if(line.toLowerCase().trim().equals("")){
                    option = -1;
                    printMenu(serverPrintOut);
                }


                //PROCESSING 


                //Search
                if(option == 1){
                    System.out.println("Searching for: " + line);


                    clearClient(serverPrintOut);
                    serverPrintOut.write(27);
                    serverPrintOut.println("[1;0HResults for: " + line + " (Press enter to continue...)");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[3;0H" + searchDatabase(line));
                }
                
                //Insert
                if(option == 2){
                    System.out.println("Inserting: " + line);

                    writeToDatabase(line);

                    clearClient(serverPrintOut);
                    serverPrintOut.write(27);
                    serverPrintOut.println("[1;0HSuccessfully inserted: " + line + " (Press enter to continue...)");
                }

                //Delete 
                if(option == 3){
                    System.out.println("Deleting: " + line);

                    if (deleteFromDatabase(line))
                        {   
                            clearClient(serverPrintOut);
                            serverPrintOut.write(27);
                            serverPrintOut.println("[1;0HSuccessfully deleted: " + line + " (Press enter to continue...)");
                        } else {
                            clearClient(serverPrintOut);
                            serverPrintOut.write(27);
                            serverPrintOut.println("[1;0HNo entry that matches the date: " + line + " (Press enter to continue...)");
                        }

                    

                }


                //INPUT

                //Search
                if(line.toLowerCase().trim().equals("1")) {
                    serverPrintOut.write(27);
                    serverPrintOut.println("[3;0HYou selected: 1. Search record.");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[5;0HEnter Appointee name to search for:");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[6;0H");

                    option = 1;

                }


                //Insert
                if(line.toLowerCase().trim().equals("2")) {
                    serverPrintOut.write(27);
                    serverPrintOut.println("[3;0HYou selected: 2. Insert new record.");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[5;0HEnter record details (yyyy-mm-dd, hh-mm, Appointee).");
                    serverPrintOut.write(27);
                    serverPrintOut.println("[6;0H");

                    option = 2;


                }

                //Delete
                if(line.toLowerCase().trim().equals("3")) {
                    serverPrintOut.write(27);
                    serverPrintOut.println("[3;0HYou selected: 3. Delete record.");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[5;0HEnter meeting to delete (yyyy-mm-dd, hh-mm)");

                    serverPrintOut.write(27);
                    serverPrintOut.println("[6;0H");

                    option = 3;
                }
                


                if(line.toLowerCase().trim().equals("menu")) {
                    printMenu(serverPrintOut);
                }

                if(line.toLowerCase().trim().equals("quit")) {
                    System.out.println("Client requested quit");
                    done = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearClient(PrintWriter serverPrintOut){ 
        serverPrintOut.write(27);
        serverPrintOut.println("[2J");
    }

    public static void printMenu(PrintWriter serverPrintOut){
             clearClient(serverPrintOut);
            serverPrintOut.write(27);
            serverPrintOut.println("[1;0HServer: Hi there!");
            serverPrintOut.write(27);
            serverPrintOut.println("[3;0HAVAILABLE COMMANDS");
            serverPrintOut.write(27);
            serverPrintOut.println("[4;0H1. \t Search record.");
            serverPrintOut.write(27);
            serverPrintOut.println("[5;0H2. \t Insert new record.");
            serverPrintOut.write(27);
            serverPrintOut.println("[6;0H3. \t Delete record.");
            serverPrintOut.write(27);
            serverPrintOut.println("[8;0HEnter command: ");
            serverPrintOut.write(27);
            serverPrintOut.println("[9;0H");

            serverPrintOut.write(27);
            serverPrintOut.print("[2J");
    }
}