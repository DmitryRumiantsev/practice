
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.*;

 class Main {

     private static final int MAX_FIELD_LENGTH = 140;
     private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

     private static ArrayList<String> warnings;
     private static StringBuffer query;
     private static ArrayList<Message> history;

     public static void main(String args[]) {

         history = new ArrayList<>();
         warnings = new ArrayList<>();
         query = new StringBuffer("");

         Handler fileHandler = null;
         PrintWriter writer = null;
         PrintStream outPrintStream = null;
         BufferedReader reader = null;

         LOGGER.setUseParentHandlers(false);
         System.setProperty("java.util.logging.SimpleFormatter.format", "%4$-7s   %5$s %6$s%n");

         try {
             fileHandler = new FileHandler("logfile.txt", true);

             writer = new PrintWriter("logfile.txt");
             writer.print("");
             writer.close();

             fileHandler.setFormatter(new SimpleFormatter());
             LOGGER.addHandler(fileHandler);
             LOGGER.setLevel(Level.INFO);

             outPrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream("logfile.txt", true)));  // append is true
             System.setErr(outPrintStream);    // redirect System.err

             reader = new BufferedReader(new InputStreamReader(System.in));
             boolean isWorking = true;
             String help = "Type 'add' to add message, 'remove' to remove message by identifier, 'load' to load from file,\n " +
                     "'save' to save to file. Type 'view' to view history or 'viewTimeRange' to view time range. Type 'searchByAuthor',\n 'searchByLexeme' or 'searchByRegex' to " +
                     "search by author, lexeme or regular expression. Type 'help' to see this message again. Type 'exit' to exit.";
             System.out.println(help);

             while (isWorking) {

                 warnings.clear();
                 query = new StringBuffer("");

                 String command = reader.readLine();
                 query.append("Query: ").append(command);
                 try {
                     switch (command) {
                         case "add": {
                             history.add(new Message(readParameter(reader, "message"), readParameter(reader, "author")));
                             break;
                         }
                         case "remove": {
                             int sizeBefore = history.size();
                             ChatHistoryOperations.removeMessageById(readParameter(reader, "id"), history);
                             query.append(history.size() != sizeBefore ? ". Removed 1 message" : ". Removed 0 messages");
                             break;
                         }
                         case "load": {
                             Reader reader1 = new FileReader(new File("history.json"));

                             query.append(". Removed ").append(history.size()).append(" messages");
                             warnings.addAll(ChatHistoryOperations.loadHistory(reader1, history));
                             query.append(". Added ").append(history.size()).append(" messages.");

                             reader1.close();
                             break;
                         }
                         case "save": {
                             Writer writer1 = new PrintWriter(new FileOutputStream(new File("history.json")));
                             ChatHistoryOperations.storeHistory(writer1, history);
                             writer1.close();
                             break;
                         }
                         case "view": {
                             ChatHistoryOperations.viewHistory(history);
                             query.append(". Viewed " + history.size() + " messages.");
                             break;
                         }
                         case "exit":
                             isWorking = false;
                             break;
                         case "searchByAuthor": {
                             String author = readParameter(reader, "author");
                             LinkedList<Message> messagesFound = ChatHistoryOperations.searchByParameter(author, "by author", history);
                             query.append(". Found ").append(messagesFound.size()).append(" messages");
                             break;
                         }
                         case "viewTimeRange": {
                             if (history.size() == 0) {

                                 System.out.println("History is empty");
                                 query.append(". Viewed 0 messages");
                             } else {
                                 String min = readParameter(reader, "lower bound");
                                 String max = readParameter(reader, "upper bound");
                                 LinkedList<Message> messagesFound = ChatHistoryOperations.viewTimeRange(new StringBuffer(min), new StringBuffer(max), history);

                                 query.append(". Viewed " + messagesFound.size() + " messages.");
                             }
                             break;
                         }
                         case "searchByLexeme": {
                             String lexeme = readParameter(reader, "lexeme");
                             LinkedList<Message> messagesFound = ChatHistoryOperations.searchByParameter(lexeme, "by lexeme", history);
                             query.append(". Found ").append(messagesFound.size()).append(" messages");
                             break;
                         }
                         case "searchByRegex": {
                             String pattern = readParameter(reader, "pattern");
                             LinkedList<Message> messagesFound = ChatHistoryOperations.searchByParameter(pattern, "by regex", history);
                             query.append(". Found ").append(messagesFound.size()).append(" messages");
                             break;
                         }
                         case "help":
                             System.out.println(help);
                             break;
                         default:
                             System.out.println("Invalid command, try again");
                             break;
                     }
                     LOGGER.log(Level.INFO, query.toString());
                     for (String warning : warnings) {
                         LOGGER.log(Level.WARNING, warning);
                     }
                 } catch (Exception ex) {
                     LOGGER.log(Level.INFO, query.toString());
                     System.out.println("Exception: " + ex.getMessage());
                     LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                     for (String warning : warnings) {
                         LOGGER.log(Level.WARNING, warning);
                     }
                 }
             }
         } catch (IOException ex) {
             LOGGER.log(Level.INFO, query.toString());
             System.out.println("Exception: " + ex.getMessage());
             LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
             for (String warning : warnings) {
                 LOGGER.log(Level.WARNING, warning);
             }
         } finally {
             try {
                 if (writer != null)
                     writer.close();
                 if (outPrintStream != null)
                     outPrintStream.close();
                 if (fileHandler != null)
                     fileHandler.close();
                 if (reader != null)
                     reader.close();
             } catch (IOException ex) {
                 System.out.println("Exception: " + ex.getMessage());
                 LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
             }
         }
     }

     private static String readParameter(BufferedReader reader, String name) throws IOException {
         if (name.equals("lower bound") || name.equals("upper bound")) {
             System.out.println("Input " + name + "( yyyy-mm-dd hh:mm:ss.[fff...] format, minutes and seconds may be omited)");
         } else {
             System.out.println("Input " + name);
         }
         String parameter = reader.readLine();
         if (((name.equals("author")) || name.equals("message")) && (parameter.length() > MAX_FIELD_LENGTH)) {
             warnings.add("Author name is too long");
             System.out.println("Warning: author name is too long");
         }
         query.append(", " + name + ": ").append(parameter);
         return parameter;
     }
 }
