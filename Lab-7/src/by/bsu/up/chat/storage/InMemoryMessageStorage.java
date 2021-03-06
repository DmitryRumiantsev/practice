package by.bsu.up.chat.storage;

import by.bsu.up.chat.common.models.Message;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.logging.impl.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Collections;

public class InMemoryMessageStorage implements MessageStorage {

    private static final String DEFAULT_PERSISTENCE_FILE = "messages.srg";

    private static final Logger logger = Log.create(InMemoryMessageStorage.class);

    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    public synchronized List<Message> getPortion(Portion portion) {
        int from = portion.getFromIndex();
        if (from < 0) {
            throw new IllegalArgumentException(String.format("Portion from index %d can not be less then 0", from));
        }
        int to = portion.getToIndex();
        if (to != -1 && to < portion.getFromIndex()) {
            throw new IllegalArgumentException(String.format("Porting last index %d can not be less then start index %d", to, from));
        }
        to = Math.max(to, messages.size());
        return messages.subList(from, to);
    }

    public InMemoryMessageStorage() {
        loadMessages();
    }

    public void storeMessages()  {
        try {
            Gson gson = new GsonBuilder().create();
            Writer writer = new PrintWriter(new FileOutputStream(new File(DEFAULT_PERSISTENCE_FILE)));
            gson.toJson(messages, writer);
            writer.close();
        }
        catch (IOException e){

        }
    }
    public  void loadMessages()  {
        try {
            Reader reader = new FileReader(new File(DEFAULT_PERSISTENCE_FILE));
            Gson gson = new GsonBuilder().create();
            Message[] arr1;
            arr1 = gson.fromJson(reader, Message[].class);
            messages.clear();
            Collections.addAll(messages, arr1);
            for(Message current:messages){

               // current.setType("othersMessage");
                if(current.getType().equals("yourMessage"))
                    current.setType("othersMessage");
                if(current.getType().equals("yourMessageChanged"))
                    current.setType("messageChanged");
                if(current.getType().equals("yourMessageDeleted"))
                    current.setType("messageDeleted");
            }
        }
        catch (IOException e){

        }
    }
    @Override
    public void addMessage(Message message)  {
        messages.add(message);
        storeMessages();
    }

    @Override
    public boolean updateMessage(Message message) {
        boolean result=false;
        for(Message currentMessage: messages) {
            if (currentMessage.getId().equals(message.getId())&& !currentMessage.getType().contains("MessageDeleted")) {
                currentMessage.setText(message.getText());
                if(message.getType().equals("fake")) {

                    currentMessage.setType("yourMessageChanged");
                }
                else{
                    currentMessage.setType(message.getType());
                }
                result=true;
            }
        }
        storeMessages();
       return result;
    }

    @Override
    public synchronized boolean removeMessage(String messageId) {
        for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();) {
            Message current = iterator.next();
            if (current.getId().equals(messageId)) {
                current.setText("message was deleted");
                current.setType("yourMessageDeleted");
                return true;
            }
        }
        storeMessages();
        return false;
    }

    @Override
    public int size() {
        return messages.size();
    }
}
