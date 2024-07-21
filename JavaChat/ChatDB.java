package JavaChat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


public class ChatDB {
    private static final String url = "jdbc:mysql://localhost:3306/JavaChat";
    private Connection con;

    public boolean connect(String user, String password) {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch ( SQLException ex ) {
            System.err.println("Can't connect");
            return false;
        }
        return true;
    }

    public void disconnect() {
        try {
            con.close();
        } catch ( SQLException ex) {
        } catch ( NullPointerException ex) {}
    }
    
    protected static boolean loadDriver() {  
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch ( ClassNotFoundException ex) {
            System.err.println("Can't load driver");
            return false;
        }
        return true;
    }

    public int getTotalMessages() {
        String query = "SELECT count(*) FROM JavaChat.chat;";

        int count = 0;
        ResultSet rs;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(query);
            rs.next();
            count = rs.getInt(1);
        } catch ( SQLException ex) {
            System.err.println("Can't use getTotalMessages");
        }

        return count;
    }

    public List<ChatMessage> getMessages(int count, int from) {
        String query = String.format(
            "SELECT * FROM JavaChat.chat\n" +
            "ORDER BY date_time\n" +
            "LIMIT %d OFFSET %d;", 
            count, from
        );

        List<ChatMessage> messages = new LinkedList<ChatMessage>();
        ResultSet rs;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                messages.add(new ChatMessage(rs));
            }
        } catch ( SQLException ex) {
            System.err.println("Can't use getMessages");
            return messages;
        }

        return messages;
    }

    public boolean postMessage(ChatMessage message) {
        String query = String.format(
            "INSERT INTO JavaChat.chat(sender, content)\n" +
            "VALUES (\"%s\", \"%s\");",
            message.sender, message.content
        );

        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
        } catch ( SQLException ex) {
            System.err.println("Can't use postMessage" + ' ' + ex.toString());
            return false;
        }

        return true;
    }
}
