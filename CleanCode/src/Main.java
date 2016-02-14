/**
 * Created by User on 11.02.2016.
 */
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.regex.*;

public class Main {

    public static ArrayList<Message> history;
    public static void addMessage(BufferedReader reader) throws IOException
    {
        System.out.print("Input author");
        String author = reader.readLine();
        System.out.print("Input message");
        String message=reader.readLine();
        history.add(new Message(message,author));
    }
    public static void storeHistory() throws IOException
    {
        try (Writer writer = new PrintWriter(new FileOutputStream(new File("history.json") )) ) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(history,writer);
        }
    }
    public static void removeMessageById(BufferedReader reader) throws IOException
    {
        System.out.println("Input id");
        String id=reader.readLine();

        for(int i=0; i<history.size();i++)
        {
            if(history.get(i).id.toString().equals(id))
                history.remove(i);
        }
    }
    public static void searchByAuthor(BufferedReader reader) throws IOException
    {
        System.out.println("Input author");
        String author=reader.readLine();
        for(Message message:history)
            if(message.author.equals(author))
                System.out.print(message+"\n");
    }
    public static void searchByLexeme(BufferedReader reader) throws IOException
    {
        System.out.println("Input lexeme");
        String lexeme=reader.readLine();
        for(Message message:history)
            if(message.toString().contains(lexeme))
                System.out.print(message+"\n");
    }
    public static void searchByRegex(BufferedReader reader) throws IOException
    {
        System.out.println("Input pattern");
        String patternString=reader.readLine();
        Pattern pattern=Pattern.compile(patternString);
        for(Message message:history)
        {
            Matcher matcher=pattern.matcher(message.toString());
            if(matcher.find(0))
                System.out.println(message);
        }

    }
    public static void loadHistory() throws IOException
    {

        try (Reader reader = new FileReader(new File("history.json"))){
                Gson gson = new GsonBuilder().create();
                Message[] arr1;
                arr1= gson.fromJson(reader, Message[].class);
                history.clear();
                for (Message message:arr1)
                    history.add(message);
        }
    }
    public static void viewHistory()
    {
        for(Message message: history)
            System.out.println(message+"\n");
    }
    public static void viewTimeRange(BufferedReader reader) throws IOException
    {
        System.out.println("Input lower bound (either yyyy-mm-dd hh:mm:ss or yyyy-mm-dd hh:mm:ss.[fff...] format)");
        String lowerBound=reader.readLine();
        Timestamp min=Timestamp.valueOf(lowerBound);
        System.out.println("Input upper bound (either yyyy-mm-dd hh:mm:ss or yyyy-mm-dd hh:mm:ss.[fff...] format)");
        String upperBound=reader.readLine();
        Timestamp max=Timestamp.valueOf(upperBound);
        for(Message message: history)
            if(!message.timestamp.before(min)&&!message.timestamp.after(max))
                System.out.println(message+"\n");
    }
    public static void main(String args[]) throws IOException
    {
        history=new ArrayList<Message>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean isWorking=true;
        String help="Type 'add' to add message, 'removeById' to remove message by identifyer, 'load' to load from file,\n " +
                "'save' to save to file. Type 'view' to view history or 'viewTimeRange' to view time range. Type 'searchByAuthor',\n 'searchByLexeme' or 'searchByRegex' to " +
                "search by author, lexeme or regular expression. Type 'help' to see this message again. Type 'exit' to exit.";
        System.out.println(help);
        while(isWorking) {
            String command=reader.readLine();
            switch(command)
            {
                case "add": addMessage(reader);
                    break;
                case "remove": removeMessageById(reader);
                    break;
                case "load": loadHistory();
                    break;
                case "save":storeHistory();
                    break;
                case "view":viewHistory();
                    break;
                case "exit": isWorking=false;
                    break;
                case "searchByAuthor": searchByAuthor(reader);
                    break;
                case "viewTimeRange": viewTimeRange(reader);
                    break;
                case "searchByLexeme": searchByLexeme(reader);
                    break;
                case "searchByRegex": searchByRegex(reader);
                    break;
                case "help": System.out.println(help);
                    break;
                default: System.out.println("Invalid command, try again");
                    break;

            }
        }
    }
}
