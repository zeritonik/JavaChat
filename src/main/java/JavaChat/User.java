package JavaChat;

import java.util.List;


public class User {
    protected ChatDB db;

    private final String user;
    private final String password;

    private ChatChat chat = null;

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
    }

    synchronized public boolean connect() {
        db = new ChatDB();
        return db.connect(user, password);
    }

    synchronized public void disconnect() {
        db.disconnect();
    }

    public boolean getState() {
        return state;
    }

    public final String getName() {
        return user;
    }

    public List<ChatMessage> getMessages() {
        return db.getMessages(chat.id, messagesCount, messagesStart);
    }

    synchronized public boolean sendMessage(String content) {
        content = content.trim();
        if (content.isEmpty())
            return true;

        ChatMessage message = new ChatMessage();
        message.sender = user;
        message.content = content;
        message.chat = chat.id;
        return db.postMessage(message);
    }

    synchronized public int moveMessageStart(int d) {
        int old = messagesStart;
        messagesStart = Math.max(0, Math.min(db.getTotalMessages(chat.id) - messagesCount, messagesStart + d));
        return messagesStart - old;
    }

    synchronized public void toLastMessages() {
        messagesStart = Math.max(0, db.getTotalMessages(chat.id) - messagesCount);
    }

    public final ChatChat getCurrentChat() {
        return chat;
    }

    synchronized public void changeCurrentChat(ChatChat chat) {
        this.chat = chat;
        if (chat != null)
            toLastMessages();
    }
    
    public List<ChatChat> getChats() {
        return db.getChats();
    }

    public List<String> getChatMembers() {
        return db.getChatMembers(chat.id);
    }

    synchronized public boolean createChat(String chat_name) {
        ChatChat newChat = new ChatChat();
        newChat.chat_name = chat_name.trim();
        return db.createChat(newChat);
    }

    synchronized public boolean addChatMember(String member) {
        return db.addChatMember(chat, member.trim());
    }

    synchronized public boolean removeChatMember(String member) {
        return db.removeChatMember(chat, member.trim());
    }

    synchronized public boolean leaveChat(ChatChat chat) {
        return db.removeChatMember(chat, user);
    }
}
