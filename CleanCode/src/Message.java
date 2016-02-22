import java.sql.Timestamp;
import java.util.Comparator;
import java.util.UUID;
class Message implements Comparator<Message> {


    private final UUID id;
    private final String author;
    private final Timestamp timestamp;
    private final String message;

    public Message(String message,String author)
    {
        this.id=UUID.randomUUID();
        this.message=message;
        this.author=author;
        this.timestamp=new Timestamp(System.currentTimeMillis());
    }

    public UUID getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message && id.equals(((Message) (obj)).id);
    }

    public int compare(Message message1, Message message2)
    {
        return message1.timestamp.compareTo(message2.timestamp);
    }



    @Override
    public String toString() {
        return id+" - "+author+" - "+timestamp+" - "+message;
    }
}
