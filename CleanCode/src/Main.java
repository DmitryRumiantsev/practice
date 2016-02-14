/**
 * Created by User on 11.02.2016.
 */
import java.io.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Main {

    public static ArrayList<Message> history;
    public static void addMessage(BufferedReader br) throws IOException
    {
        System.out.print("Enter author\n");
        String author = br.readLine();
        System.out.print("Enter message:\n");
        String message=br.readLine();
        history.add(new Message(message,author));
    }
    public static void storeHistory() throws IOException
    {
        try (Writer writer = new PrintWriter(new FileOutputStream(new File("history.json") )) ) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(history,writer);
        }
    }
    public static void removeMessageById(BufferedReader br) throws IOException
    {
        System.out.println("Input id");
        String id=br.readLine();

        for(int i=0; i<history.size();i++)
        {
            if(history.get(i).id.toString().equals(id))
                history.remove(i);
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
    public static void main(String args[]) throws IOException
    {
        history=new ArrayList<Message>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean isWorking=true;
        while(isWorking) {
            System.out.println("Type 'add' to add message, 'removeById' to remove message by identifyer, 'load' to load from file,\n " +
                    "'save' to save to file and 'view' to view history. Type 'exit' to exit.");
            String command=br.readLine();
            switch(command)
            {
                case "add": addMessage(br);
                    break;
                case "remove": removeMessageById(br);
                    break;
                case "load": loadHistory();
                    break;
                case "save":storeHistory();
                    break;
                case "view":viewHistory();
                    break;
                case "exit": isWorking=false;
                    break;
                default: System.out.println("Invalid command, try again");
                    break;

            }
        }
    }
}
