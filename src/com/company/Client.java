package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;

    final static CharSequence pipe  = "|";

    public static void main(String[] args) {
        Client client = new Client("localhost", 5056);
        client.Run();
    }

    private void Run() {
        String input;
        String reply;
        Scanner scn = new Scanner(System.in);

        while(true){
            input = scn.nextLine();

            //Check if exit
            if(input.toLowerCase().equals("exit")) {
                try {
                    dos.writeUTF("exit");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Disconnecting...");
                break;
            }

            //Send input
            try {
                //Is this gonna writeUTF even if throws exception?
                dos.writeUTF(parseInput(input));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidInputFormatException e) {
                System.out.println(e.getMessage());
                continue;
            }

            //Receive reply (assumes that every input will get a reply)
            try{
                reply = dis.readUTF();
                System.out.println(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        disconnectClient();
    }

    private void testInput(ArrayList<String> finalString) throws InvalidInputFormatException {
        finalString.set(0, finalString.get(0).toUpperCase());

        switch (finalString.get(0)) {
            case "LOGOUT":
            case "INBOX":
            case "OUTBOX":
            case "LISTUSERS":
                if(finalString.size() != 1){
                    throw new InvalidInputFormatException();
                }
                break;
            case "LOGIN":
            case "SEND":
                if (finalString.size() != 3) {
                    throw new InvalidInputFormatException();
                }
                break;
            case "CREATEUSER":
                if (finalString.size() != 8) {
                    throw new InvalidInputFormatException();
                }
                break;
            case "REMOVEUSER":
                if (finalString.size() != 2) {
                    throw new InvalidInputFormatException();
                }
                break;
            default:
                throw new InvalidInputFormatException();
        }
    }

    private String parseInput(String input) throws InvalidInputFormatException {
        ArrayList<String> finalString = new ArrayList<>();
        Pattern p = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"|\\S+");
        Matcher m = p.matcher(input);

        while (m.find()) {
            finalString.add(m.group(0));
        }
        //To uppercase
        finalString.set(0, finalString.get(0).toUpperCase());
        //Test for invalidity
        testInput(finalString);

        //Return protocol
        return String.join(pipe, finalString);
    }

    Client(String host_name, int port){
        try{
            InetAddress ip = InetAddress.getByName(host_name);

            s = new Socket(ip,port);

            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
         } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectClient() {
        try {
            s.close();
            dis.close();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class InvalidInputFormatException extends Exception{
    public InvalidInputFormatException(){
        super("Invalid input format");
    }

    public InvalidInputFormatException(String message){
        super(message);
    }
}