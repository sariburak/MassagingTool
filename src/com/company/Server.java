package com.company;

import java.io.*;
import java.net.*;

// Server class 
public class Server
{
    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056);
        System.out.println("Server connected");

        // running infinite loop for getting 
        // client request 
        while (true)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests 
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos);

                // Invoking the start() method 
                t.start();

            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class 
class ClientHandler extends Thread
{
    final static DataBase DB =
            new DataBase("postgres", "8853116","jdbc:postgresql://localhost:5432/MessagingTool");
    User user = null;

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;


    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run()
    {
        String received;
        String toReturn;

        while(true){
            try {
                //Gets protocol from client
                received = dis.readUTF();
                if(received.toLowerCase().equals("exit")){
                    break;
                }

                //Parse the protocol received
                toReturn = executeProtocol(received);

                //Send reply
                dos.writeUTF(toReturn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Loop ended
        try
        {
            // closing resources
            System.out.println("Deleting the thread");
            this.dis.close();
            this.dos.close();
            this.s.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private String executeProtocol(String received) {
        String result = "";
        System.out.println("Received line: " + received);

        //Parsing protocol
        String[] protocolParameters = received.split("\\|");
        switch (protocolParameters[0]){
            case "LOGIN":
                if(user != null){
                    result = "You are already logged in";
                }
                else if(protocolParameters.length != 3){
                    result = "Invalid number of parameters";
                }else if(!DB.sendUserConnectionRequest(protocolParameters[1], protocolParameters[2])){
                    result = "Invalid username or password";
                }else{
                    result = "Welcome back";
                    user = DB.getUserFromDB(protocolParameters[1]);
                }
                break;
            case "LOGOUT":
                if(user == null){
                    result = "You are not logged in";
                }else{
                    user = null;
                    result = "Goodbye";
                }
                break;
            case "INBOX":
                if(user == null){
                    result = "You are not logged in";
                }else{
                    result = DB.getInbox(user);
                }
                break;
            case "OUTBOX":
                if(user == null){
                    result = "You are not logged in";
                }else{
                    result = DB.getOutbox(user);
                }
                break;
            case "SEND":
                if(user == null){
                    result = "You are not logged in";
                }else{
                    result = "";
                    DB.createMessage(new Message(user.getUser_name(), protocolParameters[1], protocolParameters[2]));
                }
                break;
            case "CREATEUSER":
                if(user == null){
                    result = "You are not logged in";
                }else if(!user.isAdmin()){
                    result = "You do not have permission to do this action!";
                }else if(protocolParameters.length != 8){
                    result = "Invalid number of parameters";
                }else{
                    User user = new User(protocolParameters[1], protocolParameters[2],protocolParameters[3],protocolParameters[4],protocolParameters[5],protocolParameters[6],protocolParameters[7]);
                    DB.createUser(user);
                    result = "User has been created with the given information";
                }
                break;
            case "REMOVEUSER":
                if(user == null){
                    result = "You are not logged in";
                }else if(!user.isAdmin()){
                    result = "You do not have permission to do this action!";
                }else if(protocolParameters.length != 2){
                    result = "Invalid number of parameters";
                }else{
                    DB.deleteUser(protocolParameters[1]);
                    result = "User has been deleted";
                }
                break;
            case "LISTUSERS":
                if(user == null){
                    result = "You are not logged in";
                }else if(!user.isAdmin()){
                    result = "You do not have permission to do this action!";
                }else if(protocolParameters.length != 1){
                    result = "Invalid number of parameters";
                }else{
                    result = DB.userList();
                }
                break;
            default:
                result = "UnkownProtocol";
                break;
        }

        return result;
    }
} 