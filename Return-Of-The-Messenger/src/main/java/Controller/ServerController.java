package Controller;

import Model.Exceptions.PortIsNotOpenException;
import Model.Message;
import Model.User;
import View.Menu;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;


public class ServerController {

    private static ServerController instance;
    private static HashMap<String , ServerSocket> serverSockets = new HashMap<>();


    private ServerSocket serverSocket;

    {
        try {
            serverSocket = new ServerSocket(0);
            serverSockets.put("0", serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private ServerController() {
    }

    public static ServerController getInstance() {
        if (instance == null)
            instance = new ServerController();
        return instance;
    }


    //TODO call the method
    public void runServer() throws Exception {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                startNewClientThread(socket);
            }
        } catch (SocketException e) {
            ChatController.getInstance().setPortSet(false);
            throw new Exception("listening port is closed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startNewClientThread(Socket socket) {
        Runnable runnable = () -> {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                processInput(dataInputStream);
                dataInputStream.close();
                socket.close();
            } catch (EOFException | SocketException e) {
                System.out.println("Client " + socket.getPort() + " Disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }


    private void processInput(DataInputStream dataInputStream) {
        while (true) {
            try {
                String input = dataInputStream.readUTF();
                String[] parts = input.split("<@@>");
                String username = parts[0];
                String host = parts[1];
                int portNumber = Integer.parseInt(parts[2]);
                String messageText = parts[3];

                if (Menu.getCurrentUser().getContactByUsername(username) != null)
                    Menu.getCurrentUser().getContacts().remove(Menu.getCurrentUser().getContactByUsername(username));
                User user = new User(username, "");
                user.setHost(host);
                user.setPortNumber(portNumber);
                Menu.getCurrentUser().getContacts().add(user);
                Message message = new Message(user, messageText);
                message.setReceivingTime();
                Menu.getCurrentUser().addReceivedMessage(message);
                ChatController.getInstance().receiveMessage(username + " -> " + messageText);
            } catch (Exception ignored) {
                break;
            }
        }
    }


    //TODO check if all the code lines are needed
    public void setPort(int portNumber) {
        try {
            serverSocket.close();
            ProgramRunner.serverThread.stop();
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            serverSocket = new ServerSocket(portNumber, 100, addr);
            serverSockets.put(Integer.toString(portNumber), serverSocket);
            ProgramRunner.serverThread = new Thread(() -> {
                try {
                    ServerController.getInstance().runServer();
                } catch (Exception e) {
                    CommandHandler.setException(e);
                }
            });
            ProgramRunner.serverThread.start();
            System.gc();
            Menu.getCurrentUser().setPortNumber(portNumber);
            Menu.getCurrentUser().setHost(serverSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            CommandHandler.setException(new Exception(e.getMessage()));
        }
    }

    public void rebindPort(int portNumber) {
        try {
            ProgramRunner.serverThread.stop();
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            serverSocket = new ServerSocket(portNumber, 100, addr);
            serverSockets.put(Integer.toString(portNumber), serverSocket);
            ProgramRunner.serverThread = new Thread(() -> {
                try {
                    ServerController.getInstance().runServer();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
            ProgramRunner.serverThread.start();
            Menu.getCurrentUser().setPortNumber(portNumber);
            Menu.getCurrentUser().setHost(serverSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePort(int portNumber) {
        try {
            ServerSocket serverSocket = serverSockets.get(Integer.toString(portNumber));
            if (serverSocket.isClosed()) {
                CommandHandler.setException(new PortIsNotOpenException());
                return;
            }
            serverSocket.close();
            serverSockets.remove(Integer.toString(portNumber));
        } catch (Exception e) {
            CommandHandler.setException(new PortIsNotOpenException());
        }
    }


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(7755);
            System.out.println(serverSocket.getInetAddress().getHostName());
            Socket socek = new Socket("localhost", 7755);
            System.out.println(socek.getInetAddress().getHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
