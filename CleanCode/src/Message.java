import java.sql.Timestamp;
import java.util.Comparator;
import java.util.UUID;
class Message implements Comparator<Message> {
    final UUID id;
    final String author;
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Message && id.equals(((Message) (obj)).id);
    }
    public int compare(Message message1, Message message2)
    {
        return message1.timestamp.compareTo(message2.timestamp);
    }
    final Timestamp timestamp;
    private final String message;
    public Message(String message,String author)
    {
        this.id=UUID.randomUUID();
        this.message=message;
        this.author=author;
        this.timestamp=new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return id+" - "+author+" - "+timestamp+" - "+message;
    }
}
