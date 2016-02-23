
import java.io.*;
import java.util.ArrayList;
import com.google.gson.*;
import java.util.LinkedList;
import java.util.logging.*;

 class Main {

     private static final int MAX_FIELD_LENGTH=140;
     private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

     private static ArrayList<String> warnings;
     private static StringBuffer query;
     private static ArrayList<Message> history;

    private static void addMessage(BufferedReader reader) throws IOException
    {
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

            history.add(new Message(message, author));

            LOGGER.log(Level.INFO,query.toString());
            for(String warning:warnings) {
                LOGGER.log(Level.WARNING, warning);
            }
    }

    private static void removeMessageById(BufferedReader reader) throws IOException
    {
            System.out.println("Input id");
            String id = reader.readLine();
            query.append(", ID: ").append(id);

            LinkedList<Message> messagesToRemove=ChatHistoryOperations.removeMessageById(id,history);

            query.append(messagesToRemove.size()!=0 ? ". Removed 1 message" : "removed 0 messages");
            LOGGER.log(Level.INFO,query.toString());
    }
     private static void searchByAuthor(BufferedReader reader) throws IOException
    {
            System.out.println("Input author");
            String author = reader.readLine();
            query.append(", Author: ").append(author);

            LinkedList<Message> messagesFound=ChatHistoryOperations.searchByParameter(author,"by author",history);

            query.append(". Found ").append(messagesFound.size()).append(" messages");
            LOGGER.log(Level.INFO,query.toString());
    }
     private static void searchByLexeme(BufferedReader reader) throws IOException
    {
            System.out.println("Input lexeme");
            String lexeme = reader.readLine();
            query.append(", Lexeme: ").append(lexeme);

            LinkedList<Message> messagesFound=ChatHistoryOperations.searchByParameter(lexeme,"by lexeme",history);

            query.append(". Found ").append(messagesFound.size()).append(" messages.");
            LOGGER.log(Level.INFO,query.toString());
    }
     private static void searchByRegex(BufferedReader reader)
    {
        try {
            System.out.println("Input pattern");
            String patternString = reader.readLine();
            query.append(", Pattern: ").append(patternString);

            LinkedList<Message> messagesFound=ChatHistoryOperations.searchByParameter(patternString,"by regex",history);

            query.append(". Found ").append(messagesFound.size()).append(" messages.");
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

            ChatHistoryOperations.storeHistory(writer,history);
        }
        catch(JsonIOException|IOException ex)
        {
            LOGGER.log(Level.INFO,query.toString());
            System.out.println("Exception: "+ex.getMessage());
            LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }
     private static void loadHistory()
    {
        try (Reader reader = new FileReader(new File("history.json"))){

            query.append(". Removed ").append(history.size()).append(" messages");

            warnings.addAll(ChatHistoryOperations.loadHistory(reader,history));

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
        ChatHistoryOperations.viewHistory(history);
        LOGGER.log(Level.INFO,query.toString()+". Viewed "+history.size()+" messages.");
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
                StringBuffer lowerBound = new StringBuffer(reader.readLine());
                query.append(", min: ").append(lowerBound.toString());

                System.out.println("Input upper bound ( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
                StringBuffer upperBound = new StringBuffer(reader.readLine());
                query.append(", max: ").append(upperBound.toString());

                LinkedList<Message> messagesFound=ChatHistoryOperations.viewTimeRange(lowerBound,upperBound,history);

                LOGGER.log(Level.INFO, query.toString() + ". Viewed " + messagesFound.size() + " messages.");

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

        Handler fileHandler=null;
        PrintWriter writer=null;
        PrintStream outPrintStream=null;
        BufferedReader reader=null;

        LOGGER.setUseParentHandlers(false);
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$-7s   %5$s %6$s%n");

        try {
            fileHandler = new FileHandler("logfile.txt", true);

            writer = new PrintWriter("logfile.txt");
            writer.print("");
            writer.close();

            fileHandler.setFormatter(  new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);

            outPrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream("logfile.txt", true)));  // append is true
            System.setErr(outPrintStream);    // redirect System.err

            reader = new BufferedReader(new InputStreamReader(System.in));
            boolean isWorking = true;
            String help = "Type 'add' to add message, 'removeById' to remove message by identifier, 'load' to load from file,\n " +
                    "'save' to save to file. Type 'view' to view history or 'viewTimeRange' to view time range. Type 'searchByAuthor',\n 'searchByLexeme' or 'searchByRegex' to " +
                    "search by author, lexeme or regular expression. Type 'help' to see this message again. Type 'exit' to exit.";
            System.out.println(help);

            while (isWorking) {

                warnings.clear();
                query=new StringBuffer("");

                String command = reader.readLine();
                query.append("Query: ").append(command);

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
            LOGGER.log(Level.INFO,query.toString());
            System.out.println("Exception: "+ex.getMessage());
            LOGGER.log(Level.SEVERE,ex.getMessage(),ex);
            for(String warning:warnings) {
                LOGGER.log(Level.WARNING, warning);
            }
        }
        finally  {
            try {
                if(writer!=null)
                    writer.close();
                if(outPrintStream!=null)
                    outPrintStream.close();
                if(fileHandler!=null)
                    fileHandler.close();
                if(reader!=null)
                    reader.close();
            }
            catch (IOException ex) {
                System.out.println("Exception: " + ex.getMessage());
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
