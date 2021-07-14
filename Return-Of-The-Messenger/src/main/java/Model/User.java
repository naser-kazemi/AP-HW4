package Model;

import java.util.ArrayList;
import java.util.Comparator;

public class User {

    protected static ArrayList<User> users = new ArrayList<>();

    private final String username;
    private final String password;
    private String host;
    private int portNumber;
    private final ArrayList<User> contacts = new ArrayList<>();
    private final ArrayList<Message> receivedMessages = new ArrayList<>();
    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<User> senders = new ArrayList<>();
    private int messageCount = 0;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        users.add(this);
    }


    public static ArrayList<User> getUsers() {
        return users;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public static User getUserByUsername(String username) {
        for (User user : users)
            if (user.username.equals(username))
                return user;
        return null;
    }


    public void contactedUser(User user) {
        contacts.add(user);
    }

    public void deleteContactedUser(User user) {
        contacts.remove(user);
    }

    public ArrayList<User> getContacts() {
        return this.contacts;
    }

    public User getContactByUsername(String username) {
        for (User contact : contacts)
            if (contact.username.equals(username))
                return contact;
        return null;
    }


    public ArrayList<Message> getReceivedMessages() {
        return this.receivedMessages;
    }

    public ArrayList<Message> getSentMessages() {
        return this.sentMessages;
    }

    public void addReceivedMessage(Message message) {
        this.receivedMessages.add(message);
        if (getSenderByUsername(message.getSender().getUsername()) == null)
            this.senders.add(message.getSender());
        messageCount++;
    }


    public void addSentMessage(Message message) {
        this.sentMessages.add(message);
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void sortReceivedMessages() {
        receivedMessages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getReceivingTime().compareTo(o2.getReceivingTime());
            }
        });
    }

    public ArrayList<User> getSenders() {
        return this.senders;
    }

    public int getMessageCount() {
        return this.messageCount;
    }

    public User getSenderByUsername(String username) {
        for (User sender : this.senders)
            if (sender.username.equals(username))
                return sender;
        return null;
    }
}
