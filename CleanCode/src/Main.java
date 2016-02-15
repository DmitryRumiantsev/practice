/**
 * Created by Dmitry Rumiantsev on 11.02.2016.
 */
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.*;
import java.util.regex.*;
import java.util.logging.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static ArrayList<Message> history;
    private static ArrayList<String> warnings;
    private static StringBuffer query;
    public static void addMessage(BufferedReader reader)
    {
        try {

            System.out.println("Input author");
            String author = reader.readLine();
            if(author.length()>140) {
                warnings.add("Author name is too long");
                System.out.println("Warning: author name is too long");
            }
            System.out.println("Input message");
            String message = reader.readLine();
            if(message.length()>140) {
                warnings.add("Your message is too long");
                System.out.println("Warning: your message is too long");
            }
            query.append(", Author: "+author+", Message: "+message+". Added 1 message.");
            history.add(new Message(message, author));
            logger.log(Level.INFO,query.toString());
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
        catch (IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
    }

    public static void removeMessageById(BufferedReader reader)
    {
        try {
            System.out.println("Input id");
            String id = reader.readLine();
            query.append(", ID: "+id);
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).id.toString().equals(id))
                    history.remove(i);
            }
            query.append(". Removed 1 message");
            logger.log(Level.INFO,query.toString());
        }
        catch (IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
    public static void searchByAuthor(BufferedReader reader)
    {
        try {
            System.out.println("Input author");
            String author = reader.readLine();
            query.append(", Author: "+author);
            int found=0;
            for (Message message : history)
                if (message.author.equals(author)) {
                    System.out.print(message + "\n");
                    found++;
                }
            query.append(". Found "+found+" messages");
            logger.log(Level.INFO,query.toString());
        }
        catch (IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
    public static void searchByLexeme(BufferedReader reader)
    {
        try {
            System.out.println("Input lexeme");
            String lexeme = reader.readLine();
            query.append(", Lexeme: "+lexeme);
            int found=0;
            for (Message message : history)
                if (message.toString().contains(lexeme)) {
                    System.out.print(message + "\n");
                    found++;
                }
            query.append(". Found "+found+" messages.");
            logger.log(Level.INFO,query.toString());
        }
        catch (IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
    public static void searchByRegex(BufferedReader reader)
    {
        try {
            System.out.println("Input pattern");
            String patternString = reader.readLine();
            Pattern pattern = Pattern.compile(patternString);
            query.append(", Pattern: "+patternString);
            int found=0;
            for (Message message : history) {
                Matcher matcher = pattern.matcher(message.toString());
                if (matcher.find(0)) {
                    System.out.println(message);
                    found++;
                }
            }
            query.append(". Found "+found+" messages.");
            logger.log(Level.INFO,query.toString());
        }
        catch(IllegalArgumentException|IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }

    }
    public static void storeHistory()
    {
        try (Writer writer = new PrintWriter(new FileOutputStream(new File("history.json") )) ) {
            Gson gson = new GsonBuilder().create();
            if(history.size()==0)
            {
                warnings.add("Saving empty history will delete the current contents of the file");
                System.out.println("Warning: Saving empty history will delete the current contents of the file (you're not given a choice)");
            }
            gson.toJson(history,writer);
            logger.log(Level.INFO,query.toString());
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
        catch(JsonIOException|IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
    }
    public static void loadHistory()
    {


        try (Reader reader = new FileReader(new File("history.json"))){
                Gson gson = new GsonBuilder().create();
                Message[] arr1;
                arr1= gson.fromJson(reader, Message[].class);
                query.append(". Removed "+history.size()+" messages");
                history.clear();
                for (Message message:arr1)
                    history.add(message);
                for(int i=0;i<history.size();i++)
                    for(int j=0;j<history.size();j++)
                    {
                        if(history.get(i).equals(history.get(j))&& i!=j)
                        {
                            warnings.add("Multiple messages with the same ID are not allowed. The message '"+history.get(j)+"' was removed from history.");
                            System.out.println("Warning: Multiple messages with the same ID are not allowed.\n The message "+history.get(j)+" was removed from history.");
                            history.remove(j);
                        }
                    }
            query.append(". Added "+history.size()+" messages.");
            logger.log(Level.INFO,query.toString());
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
        catch(JsonIOException|JsonSyntaxException|IOException ex)
        {
            logger.log(Level.INFO,query.toString());
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            for(String warning:warnings)
                logger.log(Level.WARNING,warning);
        }
    }
    public static void viewHistory()
    {
        if(history.size()==0)
            System.out.println("History is empty");
        else {
            history.sort(new Message("Hello world", "John Bull"));
            for (Message message : history)
                System.out.println(message + "\n");
        }
        logger.log(Level.INFO,query.toString()+". Viewed "+history.size()+" messages.");
    }
    public static void viewTimeRange(BufferedReader reader)
    {
        if(history.size()==0) {
            System.out.println("History is empty");
            logger.log(Level.INFO, query.toString()+". Viewed 0 messages");
        }
        else {
            try {
                System.out.println("Input lower bound ( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
                String lowerBound = reader.readLine();
                StringBuffer buffer = new StringBuffer(lowerBound);
                if(lowerBound.length()>=13 && lowerBound.length()<19)
                    while(buffer.length()<19) {
                        if (buffer.length() % 3 == 1)
                            buffer.append(":");
                        else
                            buffer.append("0");
                    }
                Timestamp min = Timestamp.valueOf(buffer.toString());
                System.out.println("Input upper bound ( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
                String upperBound = reader.readLine();
                buffer.setLength(0);
                buffer.append(upperBound);
                if(lowerBound.length()>=13 && lowerBound.length()<19)
                    while(buffer.length()<19) {
                        if (buffer.length() % 3 == 1)
                            buffer.append(":");
                        else
                            buffer.append("0");
                    }
                Timestamp max = Timestamp.valueOf(buffer.toString());
                query.append(", min: " + min + ", max: " + max);
                history.sort(new Message("Hello world", "John Bull"));
                int found = 0;
                for (Message message : history)
                    if (!message.timestamp.before(min) && !message.timestamp.after(max)) {
                        System.out.println(message + "\n");
                        found++;
                    }
                logger.log(Level.INFO, query.toString() + ". Viewed " + found + " messages.");
            } catch (IllegalArgumentException | IOException ex) {
                logger.log(Level.INFO, query.toString());
                System.out.println(ex.getMessage());
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
    public static void main(String args[])
    {
        history=new ArrayList<Message>();
        warnings=new ArrayList<String>();
        query=new StringBuffer("");
        logger.setUseParentHandlers(false);
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$-7s   %5$s %6$s%n");
        try {
            Handler fileHandler = new FileHandler("logfile.txt", true);
            PrintWriter writer = new PrintWriter("logfile.txt");
            writer.print("");
            writer.close();

            fileHandler.setFormatter(  new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            PrintStream outPrintStream =
                    new PrintStream(
                            new BufferedOutputStream(
                                    new FileOutputStream("logfile.txt", true)));  // append is true
            System.setErr(outPrintStream);    // redirect System.err
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean isWorking = true;
            String help = "Type 'add' to add message, 'removeById' to remove message by identifyer, 'load' to load from file,\n " +
                    "'save' to save to file. Type 'view' to view history or 'viewTimeRange' to view time range. Type 'searchByAuthor',\n 'searchByLexeme' or 'searchByRegex' to " +
                    "search by author, lexeme or regular expression. Type 'help' to see this message again. Type 'exit' to exit.";
            System.out.println(help);
            while (isWorking) {
                warnings.clear();
                query.setLength(0);
                String command = reader.readLine();
                query.append("Query: "+command);
                switch (command) {
                    case "add":
                        addMessage(reader);
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
            System.out.println(ex.getMessage());
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
}
