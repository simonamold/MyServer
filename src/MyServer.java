import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MyServer {

        private static String ipAddr = "";
        private static String url = "";
        private static String user = "";
        private static String password = "";
        public static void main(String[] args) {

        ServerSocket serverSocket = null;
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {

            InetAddress ipAddress = InetAddress.getByName(ipAddr);
            serverSocket = new ServerSocket(6000, 50, ipAddress);
            System.out.println("Server started. Listening on port 6000.");

            while (true) {
                socket = serverSocket.accept();
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String query = (String) objectInputStream.readObject();
                System.out.println("Query received: " + query);

                if (query.toLowerCase().startsWith("insert")) {
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        PreparedStatement statement = connection.prepareStatement(query);
                        int rowsAffected = statement.executeUpdate();
                        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject("Rows affected: " + rowsAffected);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if (query.toLowerCase().startsWith("update")) {
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        PreparedStatement statement = connection.prepareStatement(query);
                        int rowsAffected = statement.executeUpdate();
                        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject("Rows affected: " + rowsAffected);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if (query.toLowerCase().startsWith("select *")) {
                    ArrayList<String> results = new ArrayList<>();
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        PreparedStatement statement = connection.prepareStatement(query);
                        ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            results.add(resultSet.getString(2) + " Year: " + resultSet.getString(4));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(results);
                    System.out.println(results);
                }
                else if (query.toLowerCase().startsWith("select ")){
                    ArrayList<String> results = new ArrayList<>();
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        PreparedStatement statement = connection.prepareStatement(query);
                        ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            results.add(resultSet.getString(1) + "/" + resultSet.getString(2) + "/"
                            + resultSet.getString(3) + "/" + resultSet.getString(4));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(results);
                    System.out.println(results);
                }
                else if (query.toLowerCase().startsWith("delete")){
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {
                        PreparedStatement statement = connection.prepareStatement(query);
                        int rowsAffected = statement.executeUpdate();
                        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject("Rows affected: " + rowsAffected);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Results sent to client.");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
