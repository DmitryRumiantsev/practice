import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.*;

public class Finder
{
    private static String parameter1;
    private static String parameter2;
    private static String mode;

    public static void setMode(String mode) {
        Finder.mode = mode;
    }

    public static void setParameter1(String parameter1) {

        Finder.parameter1 = parameter1;
    }

    public static void setParameter2(String parameter2) {
        Finder.parameter2 = parameter2;
    }

    public  static LinkedList<Message> findAll(Collection<Message> messageCollection)
    {
        LinkedList<Message> list = new LinkedList<>();
        for (Message message : messageCollection) {
            if (check(message)) {
                list.add(message);
            }
        }
        return list;
    }

    public  static LinkedList<Message> printAll(Collection<Message> messageCollection)
    {
        LinkedList<Message> list = new LinkedList<>();
        for (Message message : messageCollection) {
            if (check(message)) {
                list.add(message);
                System.out.println(message.toString());
            }
        }
        return list;
    }

    private static boolean check(Message message) throws IllegalArgumentException {

        switch (mode) {

            case "by author":
                return parameter1.equals(message.getAuthor());

            case "by id":
                return parameter1.equals(message.getId().toString());

            case "by lexeme":
                return message.toString().contains(parameter1);

            case "by regex":{
                Pattern pattern = Pattern.compile(parameter1);
                Matcher matcher = pattern.matcher(message.toString());
                return matcher.find(0);
            }

            case  "time range":
            {
                Timestamp min = Timestamp.valueOf(parameter1);
                Timestamp max = Timestamp.valueOf(parameter2.toString());
                return (!message.getTimestamp().before(min)) && (!message.getTimestamp().after(max));
            }
            default:
                return false;

        }
    }
}
