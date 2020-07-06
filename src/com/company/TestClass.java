package com.company;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {
    public static void main(String[] args) {
        Scanner myScn = new Scanner(System.in);
        String input = myScn.nextLine();


        ArrayList<String> finalString = new ArrayList<String>();
        Pattern p = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"|\\S+");
        Matcher m = p.matcher(input);

        while (m.find()) {
            finalString.add(m.group(0));
        }

        for(String pattern : finalString){
            System.out.println(pattern + "with len: " + pattern.length());
        }
    }
}

