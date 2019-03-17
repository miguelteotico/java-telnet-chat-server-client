import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    static String serverHost = "127.0.0.1";
    static int serverPort = 9876;
    static Scanner clientScanner = new Scanner(System.in);

    // generate random 5 digit number
    static int max = 99999;
    static int min = 00001;
    static Random randomNum = new Random();
    static int guestNum = min + randomNum.nextInt(max);
    static String userName = "GUEST" + guestNum;
    static String message = "";
    static Boolean requestList = false;
    
    public static void main(String[] args){
        System.out.print("Server Address (or just hit <enter> for localhost): ");
        serverHost = clientScanner.nextLine();
        if (serverHost == "") {
            serverHost = "127.0.0.1";
        }

        try{
            Socket socket = new Socket(serverHost, serverPort);
            ChatClientThread ChatClientThread = new ChatClientThread(socket, userName);
            Thread serverAccessThread = new Thread(ChatClientThread);
            serverAccessThread.start();
            ChatClientThread.sendMessage("has entered the Server");
            ChatClientThread.sendMessage("<ADDUSER>"); 
                               
            while(serverAccessThread.isAlive()){
                if(clientScanner.hasNextLine()){
                    message = clientScanner.nextLine();
                    if(message.length() >= 1){
                        if(message.contains("/exit") || message.contains("/quit") || message.contains("/logoff")){
                            ChatClientThread.sendMessage("has left the Server");  
                            try{
                                Thread.sleep(500);
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            }
                            System.exit(0);
                        }
                        else if(message.contains("/nick")){
                            String userNameOld = userName;
                            userName = message.replace("/nick ", "");
                            ChatClientThread.sendMessage("<CHANGENAME><" + userName + ">");
                            try{
                                Thread.sleep(500);
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            }
                            ChatClientThread.sendMessage("<ALL>changed username to <" + userName + ">");
                            try{
                                Thread.sleep(500);
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            }
                            ChatClientThread.changeName(userName);
                        }
                        else if(message.contains("/list")){
                            ChatClientThread.sendMessage("<LISTUSER>");
                            requestList = true;
                        }
                        else if(!message.contains("/")){
                            ChatClientThread.sendMessage("<ALL>" + message);
                        }
                    }
                }
            }
        } catch(IOException e){
            System.err.println("Fatal Connection error!");
            e.printStackTrace();
        }
    }
}

class ChatClientThread implements Runnable {
    private Socket socket;
    private String userName;
    private String message;
    private boolean hasMessage = false;

    public ChatClientThread(Socket socket, String userName){
        this.socket = socket;
        this.userName = userName;
    }

    public void sendMessage(String message){
        hasMessage = true;
        this.message = message;
    }

    public void changeName(String userName){
        this.userName = userName;
    }

    public void run(){
        System.out.println("USERNAME: " + userName);
        System.out.println("LOCAL PORT: " + socket.getLocalPort());
        System.out.println("SERVER ADDRESS: " + socket.getRemoteSocketAddress());

        try{
            PrintWriter serverWritter = new PrintWriter(socket.getOutputStream(), false);
            InputStream serverStream = socket.getInputStream();
            Scanner serverScanner = new Scanner(serverStream);
            String checkMessage = "";

            while(!socket.isClosed()){
                if(serverStream.available() > 0){
                    if(serverScanner.hasNextLine()){
                        checkMessage = serverScanner.nextLine();
                        if(checkMessage.contains("<LISTUSER>") && ChatClient.requestList){
                            System.out.println(checkMessage);
                            ChatClient.requestList = false;
                        }
                        else if(checkMessage.contains("<ALL>")){
                            checkMessage = checkMessage.replace("<ALL>", "");
                            System.out.println(checkMessage);
                        }

                    }
                }
                if(hasMessage){
                    serverWritter.println("<"+ userName + "> " + message);
                    serverWritter.flush();
                    hasMessage = false;
                }
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }
}