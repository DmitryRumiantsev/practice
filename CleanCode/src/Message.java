/**
 * Created by User on 11.02.2016.
 */
import java.sql.Timestamp;
class Message {
    String id;
    String message;
    String author;
    Timestamp timestamp;
    public Message(String id,String message,String author)
    {
        this.id=id;
        this.message=message;
        this.author=author;
        this.timestamp=new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
