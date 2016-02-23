package by.bsu.cleancode.cleancode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    private static final int MAX_FIELD_LENGTH=140;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static ArrayList<String> warnings;
    private static StringBuffer query;
    private static ArrayList<Message> history;

   private static void addMessage(BufferedReader reader) {
       try {

           System.out.println("Input author");
           String author = reader.readLine();
           if(author.length()>MAX_FIELD_LENGTH) {
               warnings.add("Author name is too long");
               System.out.println("Warning: author name is too long");
           }

           System.out.println("Input message");
           String message = reader.readLine();
           if(message.length()>MAX_FIELD_LENGTH) {
               warnings.add("Your message is too long");
               System.out.println("Warning: your message is too long");
           }

           query.append(", Author: ").append(author).append(", Message: ").append(message).append(". Added 1 message.");

           history.add(new Message(message,author));

           LOGGER.log(Level.INFO, query.toString());
           for (String warning : warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
       catch (Exception ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
           for(String warning:warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
   }
   private static void removeMessageById(BufferedReader reader)
   {
       try {
           System.out.println("Input id");
           String id = reader.readLine();
           query.append(", ID: ").append(id);

           for (int i = 0; i < history.size(); i++) {
               if (history.get(i).getId().toString().equals(id)) {
                   history.remove(i);
               }
           }

           query.append(". Removed 1 message");
           LOGGER.log(Level.INFO,query.toString());
       }
       catch (IOException ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
       }
   }
    private static void searchByAuthor(BufferedReader reader)
   {
       try {
           System.out.println("Input author");
           String author = reader.readLine();
           query.append(", Author: ").append(author);

           int messagesFound=0;
           for (Message message : history) {
               if (message.getAuthor().equals(author)) {
                   System.out.print(message + "\n");
                   messagesFound++;
               }
           }

           query.append(". Found ").append(messagesFound).append(" messages");
           LOGGER.log(Level.INFO,query.toString());
       }
       catch (IOException ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
       }
   }
    private static void searchByLexeme(BufferedReader reader)
   {
       try {
           System.out.println("Input lexeme");
           String lexeme = reader.readLine();
           query.append(", Lexeme: ").append(lexeme);

           int messagesFound=0;
           for (Message message : history) {
               if (message.toString().contains(lexeme)) {
                   System.out.print(message + "\n");
                   messagesFound++;
               }
           }

           query.append(". Found ").append(messagesFound).append(" messages.");
           LOGGER.log(Level.INFO,query.toString());
       }
       catch (IOException ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
       }
   }
    private static void searchByRegex(BufferedReader reader)
   {
       try {
           System.out.println("Input pattern");
           String patternString = reader.readLine();
           Pattern pattern = Pattern.compile(patternString);
           query.append(", Pattern: ").append(patternString);

           int messagesFound=0;
           for (Message message : history) {
               Matcher matcher = pattern.matcher(message.toString());
               if (matcher.find(0)) {
                   System.out.println(message);
                   messagesFound++;
               }
           }

           query.append(". Found ").append(messagesFound).append(" messages.");
           LOGGER.log(Level.INFO,query.toString());
       }
       catch(IllegalArgumentException|IOException ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
       }

   }
    private static void storeHistory()
   {
       try (Writer writer = new PrintWriter(new FileOutputStream(new File("history.json") )) ) {

           Gson gson = new GsonBuilder().create();

           if(history.size()==0)
           {
               warnings.add("Saving empty history will delete the current contents of the file");
               System.out.println("Warning: Saving empty history will delete the current contents of the file (you're not given a choice)");
           }
           gson.toJson(history,writer);

           LOGGER.log(Level.INFO,query.toString());
           for(String warning:warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
       catch(JsonIOException|IOException ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
           for(String warning:warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
   }
    private static void loadHistory()
   {
       try (Reader reader = new FileReader(new File("history.json"))){

           Gson gson = new GsonBuilder().create();
           Message[] arr1;
           arr1= gson.fromJson(reader, Message[].class);

           query.append(". Removed ").append(history.size()).append(" messages");

           history.clear();
           Collections.addAll(history, arr1);

               for(int i=0;i<history.size();i++) {
                   for (int j = 0; j < history.size(); j++) {
                       if (history.get(i).equals(history.get(j)) && i != j) {
                           warnings.add("Multiple messages with the same ID are not allowed. The message '" + history.get(j) + "' was removed from history.");
                           System.out.println("Warning: Multiple messages with the same ID are not allowed.\n The message " + history.get(j) + " was removed from history.");
                           history.remove(j);
                       }
                   }
               }

           query.append(". Added ").append(history.size()).append(" messages.");
           LOGGER.log(Level.INFO,query.toString());
           for(String warning:warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
       catch(Exception ex)
       {
           LOGGER.log(Level.INFO,query.toString());
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
           for(String warning:warnings) {
               LOGGER.log(Level.WARNING, warning);
           }
       }
   }
    private static void viewHistory()
   {
       if(history.size()==0) {
           System.out.println("History is empty");
       }
       else {
           history.sort(new Message("Hello world", "John Bull"));
           for (Message message : history) {
               System.out.println(message + "\n");
           }
       }

       LOGGER.log(Level.INFO,query.toString()+". Viewed "+history.size()+" messages.");
   }
    private static void prepareStringForTimestamp(StringBuffer buffer)
   {
       int startOfMinutes=13;
       int endOfSeconds=19;

       if(buffer.length()>=startOfMinutes && buffer.length()<endOfSeconds) {
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
    private static void viewTimeRange(BufferedReader reader)
   {
       if(history.size()==0) {

           System.out.println("History is empty");
           LOGGER.log(Level.INFO, query.toString()+". Viewed 0 messages");
       }
       else {
           try {
               System.out.println("Input lower bound ( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
               String lowerBound = reader.readLine();
               StringBuffer buffer = new StringBuffer(lowerBound);
               prepareStringForTimestamp(buffer);
               Timestamp min = Timestamp.valueOf(buffer.toString());

               System.out.println("Input upper bound ( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
               String upperBound = reader.readLine();
               buffer.setLength(0);
               buffer.append(upperBound);
               prepareStringForTimestamp(buffer);
               Timestamp max = Timestamp.valueOf(buffer.toString());

               query.append(", min: ").append(min).append(", max: ").append(max);

               history.sort(new Message("Hello world", "John Bull"));
               int messagesFound = 0;
               for (Message message : history) {
                   if ((!message.getTimestamp().before(min)) && (!message.getTimestamp().after(max))) {
                       System.out.println(message + "\n");
                       messagesFound++;
                   }
               }

               LOGGER.log(Level.INFO, query.toString() + ". Viewed " + messagesFound + " messages.");

           } catch (IllegalArgumentException | IOException ex) {
               LOGGER.log(Level.INFO, query.toString());
               System.out.println("Exception: "+ex.getMessage());
               LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
           }
       }
   }
   public static void main(String args[])
   {
       history= new ArrayList<>();
       warnings= new ArrayList<>();
       query=new StringBuffer("");

       LOGGER.setUseParentHandlers(false);
       System.setProperty("java.util.logging.SimpleFormatter.format", "%4$-7s   %5$s %6$s%n");

       PrintWriter writer =null;
       PrintStream outPrintStream =null;
       BufferedReader reader =null;
       Handler fileHandler =null;
       try {

           writer = new PrintWriter("logfile.txt");
           outPrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream("logfile.txt", true)));
           reader = new BufferedReader(new InputStreamReader(System.in));

           fileHandler = new FileHandler("logfile.txt", true);

           writer.print("");
           writer.close();

           fileHandler.setFormatter(  new SimpleFormatter());
           LOGGER.addHandler(fileHandler);
           LOGGER.setLevel(Level.INFO);
             // append is true
           System.setErr(outPrintStream);    // redirect System.err

           boolean isWorking = true;
           String help = "Type 'add' to add message, 'remove' to remove message by identifier, 'load' to load from file,\n " +
                   "'save' to save to file. Type 'view' to view history or 'viewTimeRange' to view time range. Type 'searchByAuthor',\n 'searchByLexeme' or 'searchByRegex' to " +
                   "search by author, lexeme or regular expression. Type 'help' to see this message again. Type 'exit' to exit.";
           System.out.println(help);

           while (isWorking) {

               warnings.clear();
               query.setLength(0);

               String command = reader.readLine();
               query.append("Query: ").append(command);

               switch (command) {
                   case "add":
                      // Message message=readMessage(reader)
                       addMessage(reader);// history.add(message);
                       break;
                   case "remove":
                       removeMessageById(reader);
                       break;
                   case "load":
                       loadHistory();
                       break;
                   case "save":
                       storeHistory();
                       break;
                   case "view":
                       viewHistory();
                       break;
                   case "exit":
                       isWorking = false;
                       break;
                   case "searchByAuthor":
                       searchByAuthor(reader);
                       break;
                   case "viewTimeRange":
                       viewTimeRange(reader);
                       break;
                   case "searchByLexeme":
                       searchByLexeme(reader);
                       break;
                   case "searchByRegex":
                       searchByRegex(reader);
                       break;
                   case "help":
                       System.out.println(help);
                       break;
                   default:
                       System.out.println("Invalid command, try again");
                       break;
               }
           }
       }
       catch (IOException ex)
       {
           System.out.println("Exception: "+ex.getMessage());
           LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
       }
       finally {
           try {
               if (writer != null)
                   writer.close();
               if (reader != null)
                   reader.close();
               if (outPrintStream != null)
                   outPrintStream.close();
               if (fileHandler != null)
                   fileHandler.close();
           }
           catch (IOException ex)
           {
               System.out.println("Exception: "+ex.getMessage());
               LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
           }
       }
   }
}
