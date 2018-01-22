package io.github.codistro.authenticate;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Server {



    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

        ServerSocket serverSocket = new ServerSocket(1002);
        Socket socket;
        Thread thread;
        MultiService service;
        while (true)
        {
            System.out.print ("Waiting for clients to connect...");
            socket = serverSocket.accept ();
            System.out.print ("Client connected...");
            service = new MultiService (socket);
            thread = new Thread (service);
            thread.start ();
        }


    }


}

class MultiService implements Runnable{
    Socket socket;
    Connection con;
    boolean result = false;
    OutputStreamWriter os;
    final String SEPARATOR = "break";
    String finalData;
    public MultiService(Socket s){
        socket = s;
    }
    @Override
    public void run() {
        try {
            final String url = "jdbc:mysql://localhost:3306/users";
            final String uname = "root";
            final String pass = "rajan@123";

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, uname, pass);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data = br.readLine();

            os = new OutputStreamWriter(socket.getOutputStream());


            String[] tableEntry = data.split(SEPARATOR);

            //Performing operation on database as per input
            if(tableEntry[1].equals("2")){ //requesting password
                result = getEmail(tableEntry[0]);
                if(!result){
                    StringBuilder sb = new StringBuilder();
                    sb.append("true").append(SEPARATOR).append(getDataByEmail(tableEntry[0]));
                    finalData = sb.toString();
                }
                else{
                    finalData = "false";
                }
                PrintWriter pw = new PrintWriter(os);
                pw.println(finalData);
                pw.flush();
            }
            else if(tableEntry[2].equals("1")) { //Logging in
                result = isValidUser(tableEntry);
                if(result){
                    StringBuilder sb = new StringBuilder();
                    sb.append("true").append(SEPARATOR).append(getDataByUsername(tableEntry[0]));
                    finalData = sb.toString();
                }
                else{
                    finalData = "false";
                }
                PrintWriter pw = new PrintWriter(os);
                pw.println(finalData);
                pw.flush();
            }else if (tableEntry[11].equals("0")) { //Registration
                String username = tableEntry[0];
                String email = tableEntry[5];

                if (getUsername(username) && getEmail(email))
                    result = true;

                if (result)
                    result = registerUser(tableEntry);

                PrintWriter pw = new PrintWriter(os);
                pw.println(result);
                pw.flush();
            }
        }
        catch (ClassNotFoundException e){

        }
        catch (IOException e){

        }
        catch (SQLException e){

        }


    }

    public boolean registerUser(String[] data) throws SQLException {
        String execute = "insert into users values (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement st = con.prepareStatement(execute);
        String date = "" + data[8] + "-" + data[7] + "-" + data[6] +"";
        st.setString(1,data[0]);
        st.setString(2,data[1]);
        st.setString(3,data[3]);
        st.setString(4,data[4]);
        st.setString(5,data[5]);
        st.setDate(6,Date.valueOf(date));
        st.setString(7,data[9]);
        st.setString(8,data[10]);
        st.execute();
        return true;
    }

    public boolean getUsername(String username) throws SQLException {
        String execute = "select * from users where username = ?";
        PreparedStatement st = con.prepareStatement(execute);
        st.setString(1,username);
        ResultSet rs = st.executeQuery();
        if(rs.next())
            return false;
        else
            return true;

    }

    public boolean getEmail(String email) throws SQLException {
        String execute = "select * from users where email = ?";
        PreparedStatement st = con.prepareStatement(execute);
        st.setString(1,email);
        ResultSet rs = st.executeQuery();
        if(rs.next())
            return false;
        else
            return true;
    }

    public boolean isValidUser(String[] data) throws SQLException {
        String username = data[0];
        String pass = data[1];

        String sql = "select * from users where username = ? and password = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1,username);
        st.setString(2,pass);
        ResultSet rs = st.executeQuery();
        if(rs.next())
            return true;
        else
            return  false;
    }

    public String getDataByUsername(String username) throws SQLException {
        String sql = "select * from users where username = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1,username);
        ResultSet rs = st.executeQuery();
        StringBuilder sb = new StringBuilder();
        while(rs.next()){
            sb.append(rs.getString("username")).append(SEPARATOR)
                    .append(rs.getString("password")).append(SEPARATOR)
                    .append(rs.getString("firstName")).append(SEPARATOR)
                    .append(rs.getString("lastName")).append(SEPARATOR)
                    .append(rs.getString("email")).append(SEPARATOR)
                    .append(rs.getString("dob")).append(SEPARATOR)
                    .append(rs.getString("question")).append(SEPARATOR)
                    .append(rs.getString("answer")).append(SEPARATOR);
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String getDataByEmail(String email) throws SQLException {
        String sql = "select * from users where email = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1,email);
        ResultSet rs = st.executeQuery();
        StringBuilder sb = new StringBuilder();
        while(rs.next()){
            sb.append(rs.getString("username")).append(SEPARATOR)
                    .append(rs.getString("password")).append(SEPARATOR)
                    .append(rs.getString("firstName")).append(SEPARATOR)
                    .append(rs.getString("lastName")).append(SEPARATOR)
                    .append(rs.getString("email")).append(SEPARATOR)
                    .append(rs.getString("dob")).append(SEPARATOR)
                    .append(rs.getString("question")).append(SEPARATOR)
                    .append(rs.getString("answer")).append(SEPARATOR);
        }
        return sb.toString();
    }
}
