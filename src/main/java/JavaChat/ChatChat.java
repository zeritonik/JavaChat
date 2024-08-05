package JavaChat;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatChat implements Cloneable, Comparable<ChatChat> {
    public final static int globalChatId = 1;
    public int id;
    public String chat_name;
    
    public ChatChat() {
        emptyInit();
    }

    public ChatChat(ResultSet rs) {
        try {
            id = rs.getInt("chat_id");
            chat_name = rs.getString("chat_name");
        } catch ( SQLException ex) {
            System.err.println("Can't parse chat: " + ex);
            emptyInit();
        }
    }

    private void emptyInit() {
        id = 0;
        chat_name = null;
    }

    @Override public ChatChat clone() {
        ChatChat res = new ChatChat();
        res.id = id;
        res.chat_name = chat_name;
        return res;
    }

    @Override public int compareTo(ChatChat chat) {
        return Integer.valueOf(id).compareTo(chat.id);
    }
}
