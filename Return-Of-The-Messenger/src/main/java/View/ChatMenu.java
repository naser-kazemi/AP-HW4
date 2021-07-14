package View;

import Controller.ChatController;
import Controller.CommandHandler;
import Controller.UserConfigController;
import Model.Exceptions.UnsuccessfulException;
import Model.Exceptions.UserHasNoAccessException;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ChatMenu extends Menu {

    private static String message = null;

    public ChatMenu() {
        this.commandPatterns = this.setCommands();
    }


    @Override
    public Pattern[] setCommands() {
        String[] commands = {"^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+$",
                "^portconfig (?=[\\w -]*--\\blisten\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b)" +
                        "(?=[\\w -]*--\\brebind\\b).+$", "^portconfig (?=[\\w -]*--\\bclose\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+"
                , "^send (?=[\\w -]*--\\bmessage \"(?<message>.*)\"?\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b)" +
                "(?=[\\w -]*--\\bhost (?<host>\\S+)?\\b).+$", "^send (?=[\\w -]*--\\bmessage \"(?<message>\\d+)\"?\\b)" +
                "(?=[\\w -]*--\\busername (?<username>\\S+)?\\b).+$", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost " +
                "(?<host>\\S+)?\\b).+$", "^send (?=[\\w -]*--\\bmessage \"(?<message>.+)\"?\\b)(?=[\\w -]*--\\bport " +
                "(?<portNumber>[\\d.]+)?\\b).+$", "^focus (?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)?\\b).+$",
                "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\bhost (?<host>\\S+)?\\b)(?=[\\w -]*--\\bport (?<portNumber>[\\d.]+)" +
                        "?\\b).+", "^focus (?=[\\w -]*--\\bstart\\b)(?=[\\w -]*--\\busername (?<username>\\S+)?\\b).+$",
                "^send (?=[\\w -]*--\\bmessage \"(?<message>.+)\"?\\b).+$", "^focus (?=[\\w -]*--\\bstop\\b).+$",
                "show (?=[\\w -]*--\\bcontacts\\b).+$", "show (?=[\\w -]*--\\bcontact (?<username>\\S+)?\\b).+$", "show " +
                "(?=[\\w -]*--\\bsenders\\b).+$", "show (?=[\\w -]*--\\bmessages\\b).+$", "show (?=[\\w -]*--\\bcount\\b)" +
                "(?=[\\w -]*--\\bsenders\\b).+$", "show (?=[\\w -]*--\\bcount\\b)(?=[\\w -]*--\\bmessages\\b).+$",
                "show (?=[\\w -]*--\\bmessages\\b)(?=[\\w -]*--\\bfrom (?<username>\\S+)?\\b).+$", "show (?=[\\w -]*--\\bcount\\b)" +
                "(?=[\\w -]*--\\bmessages\\b)(?=[\\w -]*--\\bfrom (?<username>\\S+)?\\b).+$", "^userconfig --logout$"};

        Pattern[] patterns = new Pattern[commands.length];
        for (int i = 0; i < commands.length; i++)
            patterns[i] = Pattern.compile(commands[i]);
        return patterns;
    }


    public void run(Scanner input) {
        String command = input.nextLine();
        try {
            if (!UserConfigController.isLoggedIn()) throw new UserHasNoAccessException();
            String[] parts = command.split("--");
            parts[0] = parts[0].trim();
            CommandHandler.execute(ChatController.getInstance(), command, parts[0]);
            if (CommandHandler.getException() == null) {
                if (message == null)
                    System.out.println("success");
            } else
                throw CommandHandler.getException();
        } catch (UnsuccessfulException ignored) {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        showMessage();
        CommandHandler.setException(null);
    }


    public static void main(String[] args) {
        int counter = 0;
        for (Pattern commandPattern : new ChatMenu().commandPatterns) {
            System.out.println(commandPattern);
            counter++;
        }
        System.out.println(counter);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public boolean matchesAPattern(String command) {
        for (Pattern commandPattern : this.commandPatterns)
            if (commandPattern.matcher(command).matches())
                return true;
        return false;
    }

    public static void setMessage(String message) {
        ChatMenu.message = message;
    }

    private void showMessage() {
        if (message != null)
            System.out.print(message);
        message = null;
    }

    //TODO saving user contact info

}
