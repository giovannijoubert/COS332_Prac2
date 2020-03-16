import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class MyServer {

    public static void main(String[] args) {
        connectToServer();
    }

    public static void connectToServer() {
        
        Random r = new Random();
        int port = r.nextInt(2000-1000)+1 + 1000;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Running on port: " + port);
            
            Socket connectionSocket = serverSocket.accept();
            System.out.println("Client Connected");
            
            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            
            printMenu(serverPrintOut);

            //Have the server take input from the client and echo it back
            //This should be placed in a loop that listens for a terminator text e.g. bye
            boolean done = false;
            int option = -1;

            while(!done && scanner.hasNextLine()) {
                

                String line = scanner.nextLine();
                serverPrintOut.println("Echo: " + line);


                //PROCESSING 


                //Search
                if(option == 1){
                    System.out.println("Searching for: " + line);

                    clearClient(serverPrintOut);
                    serverPrintOut.write(27);
                    serverPrintOut.println("[1;0HResults for: " + line + " (Press enter to continue...)");



                    if(line.toLowerCase().trim().equals(""))
                        printMenu(serverPrintOut);
                }
                
                //Insert
                if(option == 2){
                    System.out.println("Inserting: " + line);

                    clearClient(serverPrintOut);
                    serverPrintOut.write(27);
                    serverPrintOut.println("[1;0HSuccessfully inserted: " + line + " (Press enter to continue...)");

                    if(line.toLowerCase().trim().equals(""))
                        printMenu(serverPrintOut);
                }

                //Delete 
                if(option == 3){
                    System.out.println("Deleting: " + line);

                    clearClient(serverPrintOut);
                    serverPrintOut.write(27);
                    serverPrintOut.println("[1;0HSuccessfully inserted: " + line + " (Press enter to continue...)");

                    if(line.toLowerCase().trim().equals(""))
                        printMenu(serverPrintOut);
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