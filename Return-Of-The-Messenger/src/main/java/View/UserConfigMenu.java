package View;

import Controller.ChatController;
import Controller.Command;
import Controller.CommandHandler;
import Controller.UserConfigController;
import Model.Exceptions.UnsuccessfulException;
import Model.Exceptions.UserHasNoAccessException;

import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserConfigMenu extends Menu {

    public UserConfigMenu() {
        this.commandPatterns = this.setCommands();
    }

    @Override
    public Pattern[] setCommands() {
        String[] commands = {"^userconfig (?=[\\w -]*--\\bcreate\\b)(?=[\\w -]*--\\busername (?<username>\\S+)?\\b)" +
                "(?=[\\w -]*--\\bpassword " + "(?<password>\\S+)\\b).+$", "^userconfig (?=[\\w -]*--\\blogin\\b)" +
                "(?=[\\w -]*--\\busername (?<username>\\S+)?\\b)" + "(?=[\\w -]*--\\bpassword (?<password>\\S+)\\b).+$",
                "^userconfig --logout$"};

        Pattern[] patterns = new Pattern[commands.length];
        for (int i = 0; i < commands.length; i++)
            patterns[i] = Pattern.compile(commands[i]);
        return patterns;
    }


//    private void create(String command) throws NoUsernameAvailableException {
//        Matcher matcher = getMatcher(command, commandPatterns[0].pattern());
//        matcher.find();
//        String username = matcher.group("username");
//        String password = matcher.group("password");
//        UserConfigController.getInstance().create(username, password);
//    }
//
//
//    private void login(String command) throws UserNotFoundException, IncorrectPasswordException {
//        Matcher matcher = getMatcher(command, commandPatterns[1].pattern());
//        matcher.find();
//        String username = matcher.group("username");
//        String password = matcher.group("password");
//        UserConfigController.getInstance().login(username, password);
//    }


    private void checkForAccess(String commandName) throws UserHasNoAccessException {
        if (commandName.equals("userconfig"))
            return;
        Class<?> clazz = ChatController.class;
        for (Method method : clazz.getDeclaredMethods()) {
            try {
                if (method.getDeclaredAnnotation(Command.class).name().equals(commandName))
                    throw new UserHasNoAccessException();
            } catch (NullPointerException ignored) {

            }
        }
    }


    public void run(Scanner input) {
        String command = input.nextLine();
        try {
            String[] parts = command.split("--");
            parts[0] = parts[0].trim();
            checkForAccess(parts[0]);
            CommandHandler.execute(UserConfigController.getInstance(), command, parts[0]);
            if (CommandHandler.getException() == null)
                System.out.println("success");
            else
                throw CommandHandler.getException();
        } catch (UnsuccessfulException ignored) {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        CommandHandler.setException(null);
    }


//    public void run(Scanner input) {
//        String command = input.nextLine();
//        try {
//            if (this.commandPatterns[0].matcher(command).find() && Menu.getCurrentMenu() instanceof UserConfigMenu)
//                this.create(command);
//            else if (this.commandPatterns[1].matcher(command).find() && Menu.getCurrentMenu() instanceof UserConfigMenu)
//                this.login(command);
//            else if (this.commandPatterns[2].matcher(command).find() && Menu.getCurrentMenu() instanceof UserConfigMenu)
//                this.logout();
//            else if (!UserConfigController.isLoggedIn() && new ChatMenu().matchesAPattern(command))
//                throw new UserHasNoAccessException();
//            else
//                throw new NoSuchCommandExistsException();
//            System.out.println("success");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
