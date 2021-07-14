package Controller;

import Model.Connection;
import Model.Exceptions.*;
import Model.Message;
import Model.User;
import View.ChatMenu;
import View.Menu;
import View.UserConfigMenu;

import java.util.ArrayList;
import java.util.Comparator;

public class ChatController {

    private final ArrayList<Integer> openPorts = new ArrayList<>();
    private boolean isPortSet = false;
    private boolean isFocusOnHostMode = false;

    public void setPortSet(boolean portSet) {
        isPortSet = portSet;
    }

    private boolean isFocusOnPortMode = false;
    private String focusedHost = null;
    private int focusedPort = -1;
    private boolean isFocusOnUserMode = false;
    private User focusedUser = null;
    private int currentPort = -1;


    private static ChatController instance;

    private ChatController() {
    }


    public static ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }

        return instance;
    }


    //TODO setPort
    @Command(name = "portconfig")
    public void setPort(@Argument(name = "port", description = "number of the port to be set", hasValue = true)
                                String port, @Argument(name = "listen") String listen) {
        try {
            int portNumber = Integer.parseInt(port);
            if (isPortSet) {
                CommandHandler.setException(new PortIsAlreadySetException());
                return;
            }
            ServerController.getInstance().setPort(portNumber);
            if (CommandHandler.getException() == null) {
                openPorts.add(portNumber);
                isPortSet = true;
                currentPort = portNumber;
            }
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    //TODO rebindPort
    @Command(name = "portconfig")
    public void rebindPort(@Argument(name = "port", hasValue = true) String port, @Argument(name = "rebind") String rebind,
                           @Argument(name = "listen") String listen) {
        try {
            int portNumber = Integer.parseInt(port);
            if (openPorts.contains(portNumber)) {
                CommandHandler.setException(new PortIsAlreadySetException());
                return;
            }
            ServerController.getInstance().rebindPort(portNumber);
            if (CommandHandler.getException() == null)
                currentPort = portNumber;
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    //TODO closePort
    @Command(name = "portconfig")
    public void closePort(@Argument(name = "port", hasValue = true) String port, @Argument(name = "close") String close) {
        try {
            int portNumber = Integer.parseInt(port);
            ServerController.getInstance().closePort(portNumber);
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    //TODO
    @Command(name = "send")
    public void sendMessageByAddress(@Argument(name = "message", hasValue = true) String messageText,
                                     @Argument(name = "host", hasValue = true) String host,
                                     @Argument(name = "port", hasValue = true) String port) {
        try {
            int portNumber = Integer.parseInt(port);
            Message message = new Message(Menu.getCurrentUser(), messageText);
            Menu.getCurrentUser().addSentMessage(message);
            ClientController.getInstance().sendMessageByAddress(host, portNumber, message.getText());
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    //TODO
    @Command(name = "send")
    public void sendMessageByUsername(@Argument(name = "username", hasValue = true) String username,
                                      @Argument(name = "message", hasValue = true) String messageText) {
        User user = Menu.getCurrentUser().getContactByUsername(username);
        if (user == null) {
            CommandHandler.setException(new ContactNotFoundException());
            return;
        }
        if (Connection.getConnectionByHostAndPort(user.getHost(), user.getPortNumber()) == null)
            ClientController.getInstance().setupConnection(user.getHost(), user.getPortNumber());
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        ClientController.getInstance().sendMessageByAddress(user.getHost(), user.getPortNumber(), message.getText());
    }

    //TODO
    @Command(name = "send")
    public void sendMessageByAddressFocusedOnHost(@Argument(name = "message", hasValue = true) String messageText,
                                                  @Argument(name = "port", hasValue = true) String port) {
        try {
            int portNumber = Integer.parseInt(port);
            if (!isFocusOnHostMode) {
                CommandHandler.setException(new CouldNotSendMessageException());
                return;
            }
            Message message = new Message(Menu.getCurrentUser(), messageText);
            Menu.getCurrentUser().addSentMessage(message);
            ClientController.getInstance().sendMessageByAddress(focusedHost, portNumber, message.getText());
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }

    @Command(name = "focus")
    public void focusOnHost(@Argument(name = "host", hasValue = true) String host, @Argument(name = "start") String start) {
        isFocusOnHostMode = true;
        focusedHost = host;
    }


    @Command(name = "focus")
    public void focusOnPort(@Argument(name = "port", hasValue = true) String port) {
        try {
            int portNumber = Integer.parseInt(port);
            if (!isFocusOnHostMode && !isFocusOnUserMode) {
                CommandHandler.setException(new CouldNotSendMessageException());
                return;
            }
            isFocusOnPortMode = true;
            focusedPort = portNumber;
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    @Command(name = "focus")
    public void focusOnHostAndPort(@Argument(name = "host", hasValue = true) String host,
                                   @Argument(name = "port", hasValue = true) String port,
                                   @Argument(name = "start") String start) {
        try {
            int portNumber = Integer.parseInt(port);
            isFocusOnHostMode = true;
            focusedHost = host;
            isFocusOnPortMode = true;
            focusedPort = portNumber;
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }

    @Command(name = "focus")
    public void focusOnUser(@Argument(name = "username", hasValue = true) String username, @Argument(name = "start") String start) {
        if (Menu.getCurrentUser().getContactByUsername(username) == null) {
            CommandHandler.setException(new ContactNotFoundException());
            return;
        }
        isFocusOnUserMode = true;
        focusedUser = Menu.getCurrentUser().getContactByUsername(username);
        focusedHost = focusedUser.getHost();
        focusedPort = focusedUser.getPortNumber();
    }


    @Command(name = "send")
    public void sendMessageFullFocused(@Argument(name = "message", hasValue = true) String messageText) {
        if (!(isFocusOnUserMode || isFocusOnPortMode)) {
            CommandHandler.setException(new NotFullFoucsedException());
            return;
        }
        Message message = new Message(Menu.getCurrentUser(), messageText);
        Menu.getCurrentUser().addSentMessage(message);
        if (isFocusOnPortMode)
            ClientController.getInstance().sendMessageByAddress(focusedHost, focusedPort, message.getText());
        else
            ClientController.getInstance().sendMessageByAddress(focusedUser.getHost(), focusedUser.getPortNumber(), message.getText());

    }


    @Command(name = "focus")
    public void stopFocus(@Argument(name = "stop") String stop) {
        isFocusOnHostMode = false;
        focusedHost = null;
        isFocusOnPortMode = false;
        focusedPort = -1;
        isFocusOnUserMode = false;
        focusedUser = null;
        ClientController.getInstance().setHasSetFocusedUser(false);
        ClientController.getInstance().setHasSetFocusedAddress(false);
    }


    @Command(name = "show")
    public void showContacts(@Argument(name = "contacts") String contacts) {
        if (Menu.getCurrentUser().getContacts().isEmpty()) {
            CommandHandler.setException(new NoItemAvailableException());
            return;
        }
        StringBuilder result = new StringBuilder();
        for (User contactedUser : Menu.getCurrentUser().getContacts())
            result.append(contactedUser.getUsername()).append(" -> ").append(contactedUser.getHost())
                    .append(":").append(contactedUser.getPortNumber()).append(System.lineSeparator());
        ChatMenu.setMessage(result.toString());
    }


    @Command(name = "show")
    public void showContactByUsername(@Argument(name = "contact", hasValue = true) String contactUsername) {
        User contact = Menu.getCurrentUser().getContactByUsername(contactUsername);
        if (contact == null) {
            CommandHandler.setException(new ContactNotFoundException());
            return;
        }
        ChatMenu.setMessage(contact.getHost() + ":" + contact.getPortNumber() + System.lineSeparator());
    }


    @Command(name = "show")
    public void showSenders(@Argument(name = "senders") String sendersTag) {
        if (Menu.getCurrentUser().getSenders().isEmpty()) {
            CommandHandler.setException(new NoItemAvailableException());
            return;
        }
        StringBuilder result = new StringBuilder();
        for (User sender : Menu.getCurrentUser().getSenders())
            result.append(sender.getUsername()).append(System.lineSeparator());
        ChatMenu.setMessage(result.toString());
    }


    @Command(name = "show")
    public void showMessages(@Argument(name = "messages") String messageTag) {
        if (Menu.getCurrentUser().getReceivedMessages().isEmpty()) {
            CommandHandler.setException(new NoItemAvailableException());
            return;
        }
        Menu.getCurrentUser().sortReceivedMessages();
        StringBuilder result = new StringBuilder();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            result.append(receivedMessage.getSender().getUsername()).append(" -> ").append(receivedMessage.getText())
                    .append(System.lineSeparator());
        ChatMenu.setMessage(result.toString());
    }


    @Command(name = "show")
    public void sendersCount(@Argument(name = "senders") String sendersTag, @Argument(name = "count") String count) {
        ChatMenu.setMessage(Menu.getCurrentUser().getSenders().size() + System.lineSeparator());
    }


    @Command(name = "show")
    public void messagesCount(@Argument(name = "messages") String messageTag, @Argument(name = "count") String count) {
        ChatMenu.setMessage(Menu.getCurrentUser().getMessageCount() + System.lineSeparator());
    }


    @Command(name = "show")
    public void messagesFromUser(@Argument(name = "from", hasValue = true) String username,
                                 @Argument(name = "messages") String messagesTag) {
        User sender = Menu.getCurrentUser().getSenderByUsername(username);
        if (sender == null) {
            CommandHandler.setException(new NoItemAvailableException());
            return;
        }
        ArrayList<Message> messages = new ArrayList<>();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            if (receivedMessage.getSender().getUsername().equals(sender.getUsername()))
                messages.add(receivedMessage);
        messages.sort(Comparator.comparing(Message::getReceivingTime));
        StringBuilder result = new StringBuilder();
        for (Message message : messages)
            result.append(message.getText())
                    .append(System.lineSeparator());
        ChatMenu.setMessage(result.toString());
    }


    @Command(name = "show")
    public void messagesCountFromUser(@Argument(name = "from", hasValue = true) String username,
                                      @Argument(name = "messages") String messagesTag,
                                      @Argument(name = "count") String count) {
        User sender = Menu.getCurrentUser().getSenderByUsername(username);
        if (sender == null) {
            ChatMenu.setMessage(0 + System.lineSeparator());
        }
        ArrayList<Message> messages = new ArrayList<>();
        for (Message receivedMessage : Menu.getCurrentUser().getReceivedMessages())
            if (receivedMessage.getSender().getUsername().equals(sender.getUsername()))
                messages.add(receivedMessage);
        ChatMenu.setMessage(messages.size() + System.lineSeparator());
    }

    public void receiveMessage(String message) {
        new ChatMenu().showMessage(message);
    }


    @Command(name = "contactconfig")
    public void saveContact(@Argument(name = "username", hasValue = true) String username, @Argument(name = "host", hasValue = true)
            String host, @Argument(name = "port", hasValue = true) String port, @Argument(name = "link") String link) {
        try {
            int portNumber = Integer.parseInt(port);
            if (Menu.getCurrentUser().getContactByUsername(username) != null)
                Menu.getCurrentUser().getContacts().remove(Menu.getCurrentUser().getContactByUsername(username));
            User user = new User(username, "");
            user.setHost(host);
            user.setPortNumber(portNumber);
            Menu.getCurrentUser().getContacts().add(user);
        } catch (NumberFormatException e) {
            CommandHandler.setException(new NullPointerException("Invalid Port Number"));
        }
    }


    @Command(name = "userconfig", isLabeled = true)
    @Label(name = "logout")
    private void logout() {
        if (Menu.getCurrentMenu() instanceof UserConfigMenu) {
            CommandHandler.setException(new UserNotLoggedInException());
            return;
        }
        Menu.setLoggedIn(false);
        Menu.setCurrentUser(null);
        Menu.setCurrentMenu(new UserConfigMenu());
    }


    @Label(name = "port", description = "takes a value number of the port to be set")
    @Label(name = "rebind", description = "rebind the port")
    @Label(name = "listen", description = "specifies a port to listen")
    @Label(name = "close", description = "close the port")
    public void portconfigDummyMethodForInstruction() {
    }


    @Label(name = "start", description = "starts focusing on a target")
    @Label(name = "host", description = "selecting a host to focus on")
    @Label(name = "port", description = "selecting a port to focus on")
    @Label(name = "username", description = "selecting a username to focus on")
    public void focusDummyMethodForInstruction() {
    }


    @Label(name = "message", description = "message text")
    @Label(name = "port", description = "target user port")
    @Label(name = "host", description = "target user host")
    @Label(name = "username", description = "target user username")
    public void sendDummyMethodForInstruction() {
    }


    @Label(name = "count", description = "shows number of selected items")
    @Label(name = "from", description = "selecting a user to show items from (takes a username as parameter)")
    @Label(name = "messages", description = "selecting message as items")
    @Label(name = "senders", description = "selecting senders as items")
    @Label(name = "contact", description = "selecting a contact as items (takes a username as parameter)")
    public void showDummyMethodForInstruction() {
    }


    @Label(name = "link", description = "links a username and address to a contact")
    @Label(name = "username", description = "contact's username")
    @Label(name = "host", description = "contact's host")
    @Label(name = "port", description = "contact's port")
    public void contactconfigDummyMethodForInstruction() {
    }


    @Label(name = "logout", description = "logout the current user")
    public void userconfigDummyMethodForInstruction() {
    }


    public static void main(String[] args) {

    }

}


