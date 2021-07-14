package Model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Connection {

    private static ArrayList<Connection> connections = new ArrayList<>();


    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String host;
    private int portNumber;


    public Connection(String host ,int portNumber) throws IOException {
        this.host = host;
        this.portNumber = portNumber;
        socket = new Socket(host, portNumber);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        connections.add(this);
    }


    public Connection(Socket socket) throws IOException {
        this.host = socket.getInetAddress().getHostName();
        this.portNumber = socket.getPort();
        this.socket = socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        connections.add(this);
    }


    public static ArrayList<Connection> getConnections() {
        return connections;
    }


    public String getHost() {
        return host;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public static Connection getConnectionByHostAndPort(String host, int portNumber) {
        for (Connection connection : connections)
            if (connection.host.equals(host) && connection.portNumber == portNumber)
                return connection;
        return null;
    }
}
