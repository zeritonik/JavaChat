package JavaChat;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;


public class ChatMessage {
    public Integer id;
    public String sender;
    public Timestamp date_time;
    public String content;
    public int chat;

    public ChatMessage() {
        emptyInit();
    }

    public ChatMessage(ResultSet rs) {
        try {
            id = rs.getInt("id");
            sender = rs.getString("sender");
            date_time = rs.getTimestamp("date_time");
            content = rs.getString("content");
            chat = rs.getInt("chat");
        } catch ( SQLException ex ) {
            System.out.println("Can't parse message data");
            emptyInit();
        }
    }

    private void emptyInit() {
        id = null;
        sender = null;
        date_time = null;
        content = null;
    }
}