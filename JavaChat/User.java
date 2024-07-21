package JavaChat;

import java.util.List;


public class User {
    private ChatDB db;
    private String user;
    private String password;

    private int messagesStart;
    private final int messagesCount=100;

    private boolean state;

    public User(String user, String password) {
        this.user = user;
        this.password = password;

        state = connect();
        if (!state) {
            disconnect();
            return;
        }
        
        messagesStart = Math.max(0, db.getTotalMessages() - messagesCount + messagesCount / 12);
    }

    public boolean connect() {
        db = new ChatDB();
        return db.connect(user, password);
    }

    public void disconnect() {
        db.disconnect();
    }

    public boolean getState() {
        return state;
    }

    public List<ChatMessage> getMessages() {
        var res = db.getMessages(messagesCount, messagesStart);
        messagesStart = Math.max(0, messagesStart + res.size() - messagesCount + messagesCount / 12);
        return res;
    }

    public boolean sendMessage(String content) {
        content = content.trim();
        if (content.isEmpty())
            return true;

        ChatMessage message = new ChatMessage();
        message.sender = user;
        message.content = content;
        return db.postMessage(message);
    }
}
