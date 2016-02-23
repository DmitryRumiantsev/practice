import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class ChatHistoryOperations {

    public static LinkedList<Message> removeMessageById(String id, ArrayList<Message> history) {

        Finder.setMode("by id");
        Finder.setParameter1(id);
        LinkedList<Message> messagesToRemove=Finder.findAll(history);
        history.removeAll(messagesToRemove);
        return  messagesToRemove;
    }

    public static LinkedList<Message> searchByParameter(String parameter,String mode, ArrayList<Message> history) {

        Finder.setMode(mode);
        Finder.setParameter1(parameter);
        LinkedList<Message> messagesFound = Finder.printAll(history);
        return messagesFound;
    }

    public static void storeHistory(Writer writer,ArrayList<Message> history) {
        Gson gson = new GsonBuilder().create();
        gson.toJson(history,writer);
    }

    private static LinkedList<String> removeMessagesWithSameId(ArrayList<Message> history) {

        Finder.setMode("by id");

        LinkedList<String> warnings=new LinkedList<>();

        for(int i=0;i<history.size();i++) {

            Finder.setParameter1(history.get(i).getId().toString());
            LinkedList<Message> messagesWithSameId=Finder.findAll(history);

            if(messagesWithSameId.size()>1) {
                messagesWithSameId.pollFirst();
                String warning="Multiple messages with the same ID are not allowed. Messages " + messagesWithSameId.toString() + " were removed from history.";
                System.out.println("Warning: " +warning );
                warnings.add(warning);
                history.removeAll(messagesWithSameId);
            }
        }
        return warnings;
    }
    public static LinkedList<String> loadHistory(Reader reader, ArrayList<Message> history)
    {
        Gson gson = new GsonBuilder().create();
        Message[] arr1;
        arr1= gson.fromJson(reader, Message[].class);
        history.clear();
        Collections.addAll(history, arr1);
        return removeMessagesWithSameId(history);
    }
    public static void viewHistory(ArrayList<Message> history) {

        if(history.size()==0) {
            System.out.println("History is empty");
        }
        else {
            history.sort(new Message("Hello world", "John Bull"));
            for (Message message : history) {
                System.out.println(message + "\n");
            }
        }
    }
    private static void prepareStringForTimestamp(StringBuffer buffer)
    {
        int startOfminutes=13;
        int endOfSeconds=19;

        if(buffer.length()>=startOfminutes && buffer.length()<endOfSeconds) {
            while (buffer.length() < endOfSeconds) {
                if (buffer.length() % 3 == 1) {
                    buffer.append(":");
                }
                else {
                    buffer.append("0");
                }
            }
        }
    }
    public static LinkedList<Message> viewTimeRange(StringBuffer lowerBound, StringBuffer upperBound,ArrayList<Message> history)
    {
        prepareStringForTimestamp(lowerBound);
        prepareStringForTimestamp(upperBound);
        Finder.setMode("time range");
        Finder.setParameter1(lowerBound.toString());
        Finder.setParameter2(upperBound.toString());
        LinkedList<Message> messagesFound=Finder.printAll(history);
        return  messagesFound;
    }
}
