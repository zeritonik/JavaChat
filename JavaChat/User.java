package JavaChat;

import java.util.List;


public class User {
    private ChatDB db;
    private String user;
    private String password;

    private int messagesStart;
    public static final int messagesCount = 150;

    private boolean state;

    public User(String user, String password) {
        this.user = user;
        this.password = password;

        state = connect();
        if (!state) {
            disconnect();
            return;
        }
        
        toLastMessages();
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
        return db.getMessages(messagesCount, messagesStart);
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

    public int moveMessageStart(int d) {
        int old = messagesStart;
        messagesStart = Math.max(0, Math.min(db.getTotalMessages() - messagesCount, messagesStart + d));
        return messagesStart - old;
    }

    public void toLastMessages() {
        messagesStart = Math.max(0, db.getTotalMessages() - messagesCount);
    }
}
