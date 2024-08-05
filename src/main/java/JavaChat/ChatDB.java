package JavaChat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


public class ChatDB {
    private static final String url = "jdbc:mysql://" + ServerData.address + "/" + ServerData.dbName;
    private Connection con;

    public boolean connect(String user, String password) {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch ( SQLException ex ) {
            System.err.println("Can't connect\n" + ex);
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
    
    public static boolean loadDriver() {  
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch ( ClassNotFoundException ex) {
            System.err.println("Can't load driver\n" + ex);
            return false;
        }
        return true;
    }

    public int getTotalMessages(int chat) {
        String query = String.format("CALL getTotalMessages(%d)", chat);

        int count = 0;
        ResultSet rs;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(query);
            rs.next();
            count = rs.getInt(1);
        } catch ( SQLException ex) {
            System.err.println("Can't getTotalMessages: " + ex);
        }

        return count;
    }

    public List<ChatMessage> getMessages(int chat, int lim, int off) {
        String query = String.format("CALL getMessages(%d, %d, %d)", chat, lim, off);

        List<ChatMessage> messages = new LinkedList<ChatMessage>();
        ResultSet rs;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                messages.add(new ChatMessage(rs));
            }
        } catch ( SQLException ex) {
            System.err.println("Can't getMessages\n" + ex);
            return messages;
        }

        return messages;
    }

    public boolean postMessage(ChatMessage message) {
        String query = String.format("CALL insertMessage(\"%s\", %d)", message.content, message.chat);

        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
        } catch ( SQLException ex) {
            System.err.println("Can't postMessage" + ' ' + ex);
            return false;
        }

        return true;
    }

    public List<ChatChat> getChats() {
        String query = String.format("CALL getUserChats()");

        List<ChatChat> res = new LinkedList<ChatChat>();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                res.add(new ChatChat(rs));
            }
        } catch ( SQLException ex ) {
            System.err.println("Can't getChats: " + ex);
        }

        return res;
    }

    public List<String> getChatMembers(int chat) {
        String query = String.format("CALL getChatMembers(%d)", chat);

        List<String> res = new LinkedList<String>();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next())
                res.add(rs.getString(1));
        } catch ( SQLException ex ) {
            System.err.println("Can't getChatMembers: " + ex);
        }

        return res;
    }

    public boolean createChat(ChatChat chat) {
        String query = String.format("CALL createChat('%s')", chat.chat_name);

        try {
            Statement st = con.createStatement();
            st.executeQuery(query);
        } catch ( SQLException ex ) {
            System.err.println("Can't createChat: " + ex);
            return false;
        }

        return true;
    } 

    public boolean addChatMember(ChatChat chat, String member) {
        String query = String.format("CALL addChatMember(%d, '%s')", chat.id, member);

        try {
            Statement st = con.createStatement();
            st.executeQuery(query);
        } catch ( SQLException ex ) {
            System.err.println("Can't addChatMember: " + ex);
            return false;
        }

        return true;
    }

    public boolean removeChatMember(ChatChat chat, String member) {
        String query = String.format("CALL removeChatMember(%d, '%s')", chat.id, member);

        try {
            Statement st = con.createStatement();
            st.executeQuery(query);
        } catch ( SQLException ex ) {
            System.err.println("Can't removeChatMember: " + ex);
            return false;
        }

        return true;
    }
}