package Controller;

import Model.Exceptions.ContactNotFoundException;
import Model.Exceptions.CouldNotSendMessageException;
import Model.User;
import View.Menu;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientController {

    private static ClientController instance;


    private Socket socket = new Socket();
    private DataOutputStream dataOutputStream;
    private boolean hasSetFocusedUser = false;
    private boolean hasSetFocusedAddress = false;


    public static ClientController getInstance() {
        if (instance == null)
            instance = new ClientController();
        return instance;
    }


    public void setupConnection(String host, int portNumber) {
        try {
            socket.close();
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, portNumber), 1000);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            CommandHandler.setException(new CouldNotSendMessageException());
        }
    }


    //TODO change name
    public void sendMessageByAddress(String host, int portNumber, String messageText) {
        setupConnection(host, portNumber);
        try {
            messageText = messageText.substring(1, messageText.length() - 1);
            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
                    "<@@>" + Menu.getCurrentUser().getPortNumber() + "<@@>" + messageText);
            dataOutputStream.flush();
        } catch (Exception e) {
            CommandHandler.setException(new CouldNotSendMessageException());
        }

    }


//    public void sendMessageByUser(User user, String messageText) {
//        setupConnection(user.getHost(), user.getPortNumber());
//        try {
//            messageText = messageText.substring(1, messageText.length() - 1);
//            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
//                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
//            dataOutputStream.flush();
//        } catch (Exception e) {
//            CommandHandler.setException(new ContactNotFoundException());
//        }
//    }


//    //Todo
//    public void sendMessageByAddressFocused(String host, int portNumber, String messageText) {
//        setupConnection(host, portNumber);
//        try {
//            messageText = messageText.substring(1, messageText.length() - 1);
//            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
//                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
//            dataOutputStream.flush();
//        } catch (IOException e) {
//            CommandHandler.setException(new CouldNotSendMessageException());
//        }
//    }

//
//    public void sendMessageByUserFocused(User user, String messageText) {
////        if (!hasSetFocusedUser) {
////            setupConnection(user.getHost(), user.getPortNumber());
////            hasSetFocusedUser = true;
////        }
//        setupConnection(user.getHost(), user.getPortNumber());
//        try {
//            messageText = messageText.substring(1, messageText.length() - 1);
//            dataOutputStream.writeUTF(Menu.getCurrentUser().getUsername() + "<@@>" + Menu.getCurrentUser().getHost() +
//                    "<@@>" + Menu.getCurrentUser().getPortNumber() + messageText);
//            dataOutputStream.flush();
//        } catch (Exception e) {
//            CommandHandler.setException(new ContactNotFoundException());
//        }
//    }

    public void setHasSetFocusedUser(boolean hasSetFocusedUser) {
        this.hasSetFocusedUser = hasSetFocusedUser;
    }

    public void setHasSetFocusedAddress(boolean hasSetFocusedAddress) {
        this.hasSetFocusedAddress = hasSetFocusedAddress;
    }


}
