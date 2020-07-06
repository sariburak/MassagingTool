package com.company;

import javax.xml.transform.Result;
import java.sql.*;

public class DataBase {
    private Connection conn;
    final String db_admin;
    final String db_admin_password;
    final String url;

    public DataBase(String user, String password, String url) {
        conn = null;
        this.db_admin = user;
        this.db_admin_password = password;
        this.url = url;
    }

    public Connection ConnectDB(){
        try {
            conn = DriverManager.getConnection(url, db_admin, db_admin_password);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        System.out.println("Connection established");
        return conn;
    }

    public boolean userConnectionRequest(User user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select user_name, password from users where user_name = '"
                    + user.getUser_name() + "' AND password = '" + user.getPassword() + "'");
            return rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public String userList(){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        String result = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from" + " users");
            while(rs.next()){
                result += "-----------------------"+ rs.getString("user_name") + "---------------- \n" +
                        "Name: " + rs.getString("name") + "\n" +
                        "Surname " + rs.getString("surname") + "\n";
            }
            return result;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public boolean sendUserConnectionRequest(String username, String password){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select user_name, password from users where user_name = '"
                    + username + "' AND password = '" + password + "'");
            return rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void createUser(User user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            String sql = "insert into users (user_name, password, name, surname, gender, birthdate, email)" +
                    " values ('" + user.getUser_name() + "' , '" + user.getPassword() + "', '" + user.getName() + "', '" + user.getSurname() + "', '" +
                    user.getGender().toString() + "', '" + user.getBirthday() + "', '" + user.getEmail() + "')";

            stmt.executeUpdate(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteUser(User user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "delete from users" +
                    " where user_name = " + "'" + user.getUser_name()+ "'";
            stmt.executeUpdate(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteUser(String user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "delete from users" +
                    " where user_name = " + "'" + user + "'";
            stmt.executeUpdate(sql);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public User getUserFromDB(String user_id){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        PreparedStatement stmt = null;
        String getUserString =
                "select * from users where " +
                        "user_name = ?";
        try {
            stmt = conn.prepareStatement(getUserString);
            stmt.setString(1, user_id);

            ResultSet rs = stmt.executeQuery();
            //Need a return statement
            if(rs.next() == false){
                System.out.println("User not found");
                return null;
            }else{
                //Is admin?
                boolean admin = rs.getInt("is_admin") == 1 ? true : false;
                //Construct user with given info
                User found = new User(
                        rs.getString("user_name"), rs.getString("password"), rs.getString("birthdate"),
                        rs.getString("name"), rs.getString("surname"), rs.getString("gender"), rs.getString("email"), admin);

                return found;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //Fail
        return null;
    }

    public String getInbox(User user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        String result = "";
        PreparedStatement stmt = null;
        String statementString= "select sender, message" +
                " from messages" +
                " where receiver = ?";

        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, user.getUser_name());

            System.out.println(statementString);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                result += "-------------Sender: "  + rs.getString("sender") +"--------------------\n" +
                        rs.getString("message") + "\n";
            }
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getInbox(String username){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        PreparedStatement stmt = null;
        String statementString= "select sender, message" +
                " from messages" +
                " where receiver = ?";

        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, username);

            String result = "";
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                result += "-------------Sender: "  + rs.getString("sender") +"--------------------\n" +
                        rs.getString("message") + "\n";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getOutbox(User user){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        PreparedStatement stmt = null;
        String statementString= "select receiver, message" +
                " from messages" +
                " where sender = ?";

        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, user.getUser_name());

            String result = "";
            System.out.println(statementString);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                result += "-------------Receiver: "  + rs.getString("receiver") +"--------------------\n" +
                        rs.getString("message") + "\n";
            }
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getOutbox(String username){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }

        String result = "";
        PreparedStatement stmt = null;
        String statementString= "select receiver, message" +
                " from messages" +
                " where sender = ?";

        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, username);

            System.out.println(statementString);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                result += "-------------Receiver: "  + rs.getString("receiver") +"--------------------\n" +
                        rs.getString("message") + "\n";
            }
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void createMessage(Message message){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        PreparedStatement stmt = null;
        String statementString =
                "insert into messages (message, receiver, sender) " +
                        "values (?, ? ,?)";
        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, message.getMessage());
            stmt.setString(2, message.receiver);
            stmt.setString(3, message.sender);

            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteMessage(Message message){
        if(conn == null){
            System.out.println("Connecting...");
            ConnectDB();
        }
        PreparedStatement stmt = null;
        String statementString =
                "delete from messages " +
                        " where sender = ? AND " +
                        " receiver = ? AND" +
                        " message = ?";
        try {
            stmt = conn.prepareStatement(statementString);
            stmt.setString(1, message.sender);
            stmt.setString(2, message.receiver);
            stmt.setString(3, message.getMessage());

            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
    DataBase MessageTool = new DataBase("postgres", "8853116","jdbc:postgresql://localhost:5432/MessagingTool");
    User sariburak = MessageTool.getUserFromDB("sariburak");
    User testUser = MessageTool.getUserFromDB("testUser1");

    Message newM = new Message(testUser.getUser_name(), sariburak.getUser_name(), "Thank you very much Human Person");

    MessageTool.createMessage(newM);

    MessageTool.getInbox("sariburak");
    }*/
}


/* insert into users (user_name, password, name, surname, birthdate, gender, email, is_admin)
	values ('sariburak', '123456789', 'Burak', 'Sari', '29-08-2000', 'M','mail@mail.com', 1);*/