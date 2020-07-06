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
        String input = "";
        String reply;
        Scanner scn = new Scanner(System.in);

        while(true){
            input = scn.nextLine();

            //Send input
            try {
                dos.writeUTF(parseInput(input));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Check if exit
            if(input.toLowerCase().equals("exit")) {
                System.out.println("Disconnecting...");
                break;
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

    private String parseInput(String input) {
        ArrayList<String> finalString = new ArrayList<String>();
        Pattern p = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"|\\S+");
        Matcher m = p.matcher(input);

        while (m.find()) {
            finalString.add(m.group(0));
        }
        System.out.println(String.join(pipe, finalString));
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
