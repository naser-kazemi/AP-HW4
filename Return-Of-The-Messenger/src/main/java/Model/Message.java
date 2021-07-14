package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message {

    private final User sender;
    private ArrayList<User> receivers;
    private String text;
    private LocalDateTime receivingTime;
    private LocalDateTime sendingTime;


    public Message(User sender, String text) {
        this.sender = sender;
        this.text = text;
    }


    public void setReceivers(ArrayList<User> receivers) {
        this.receivers = receivers;
    }

    public void editText(String text) {
        this.text = text;
    }

    public User getSender() {
        return this.sender;
    }

    public ArrayList<User> getReceivers() {
        return this.receivers;
    }


    public String getText() {
        return this.text;
    }

    public LocalDateTime getReceivingTime() {
        return this.receivingTime;
    }

    public LocalDateTime getSendingTime() {
        return this.sendingTime;
    }

    public void setReceivingTime() {
        this.receivingTime = LocalDateTime.now();
    }

    public void setSendingTime() {
        this.sendingTime = LocalDateTime.now();
    }
}
