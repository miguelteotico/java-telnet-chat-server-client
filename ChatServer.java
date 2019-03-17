import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    static String serverHost = "127.0.0.1";
    static int serverPort = 9876;
    static private List<ChatServerThread> clientsList;
    static String clientsNameList = "";
 
    public static void main(String[] args){
        ChatServer chatServer = new ChatServer();
        clientsList = new ArrayList<ChatServerThread>();
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);   
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Server Address: " + ip.getHostAddress());
            while(true){
                Socket socket = serverSocket.accept();
                ChatServerThread clientServerThread = new ChatServerThread(socket, clientsList);
                Thread thread = new Thread(clientServerThread);
                thread.start();
                clientsList.add(clientServerThread);
                System.out.print("Connected to Server: " + socket.getRemoteSocketAddress());
            }
        } catch (IOException e){
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(1);
        }
    }
}

class ChatServerThread implements Runnable {
    private Socket socket;
    private PrintWriter clientWriter;
    private List<ChatServerThread> clientsList;

    public ChatServerThread(Socket socket, List<ChatServerThread> clientsList){
        this.socket = socket;
        this.clientsList = clientsList;
    }

    public void run() {
        try{
            this.clientWriter = new PrintWriter(socket.getOutputStream(), false);
            Scanner inputScanner = new Scanner(socket.getInputStream());

            while(!socket.isClosed()){
                if(inputScanner.hasNextLine()){
                    String input = inputScanner.nextLine();
                    if(input.contains("<ADDUSER>")){
                        String tempName = input;
                        tempName = tempName.replace("<ADDUSER>","");
                        tempName = tempName.replace(" ","");
                        ChatServer.clientsNameList = ChatServer.clientsNameList + tempName;
                        System.out.println(" " + tempName);
                    }
                    else{
                        if(input.contains("<LISTUSER>")){
                            input = "<LISTUSER>" + ChatServer.clientsNameList;
                        } 
                        else if(input.contains("<CHANGENAME>")){
                            String[] tokens = input.split(" <CHANGENAME>");
                            ChatServer.clientsNameList = 
                            ChatServer.clientsNameList.replace(tokens[0], tokens[1]);
                        } 
                        for(ChatServerThread clientsListLoop : clientsList){
                            PrintWriter clientWriterTemp = clientsListLoop.clientWriter;
                            if(clientWriterTemp != null){
                                clientWriterTemp.write(input + "\r\n");
                                clientWriterTemp.flush();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}