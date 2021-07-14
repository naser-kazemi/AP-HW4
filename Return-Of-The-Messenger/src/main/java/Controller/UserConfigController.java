package Controller;

import Model.Exceptions.IncorrectPasswordException;
import Model.Exceptions.NoUsernameAvailableException;
import Model.Exceptions.UserNotFoundException;
import Model.Exceptions.UserNotLoggedInException;
import Model.User;
import View.ChatMenu;
import View.Menu;
import View.UserConfigMenu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserConfigController {


    private static UserConfigController instance;
    private static boolean loggedIn = false;

    private UserConfigController() {
    }


    public static UserConfigController getInstance() {

        if (instance == null)
            instance = new UserConfigController();

        return instance;
    }

    private boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("[\\w-]+");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    @Command(name = "userconfig")
    public void create(@Argument(name = "username", hasValue = true) String username,
                       @Argument(name = "password", hasValue = true) String password,
                       @Argument(name = "create") String create) {
        if (User.getUserByUsername(username) != null || !isUsernameValid(username)) {
            CommandHandler.setException(new NoUsernameAvailableException());
            return;
        }
        new User(username, password);

    }


    @Command(name = "userconfig")
    public void login(@Argument(name = "username", hasValue = true) String username,
                      @Argument(name = "password", hasValue = true) String password,
                      @Argument(name = "login") String login) {
        if (User.getUserByUsername(username) == null) {
            CommandHandler.setException(new UserNotFoundException());
            return;
        }
        if (!User.getUserByUsername(username).getPassword().equals(password)) {
            CommandHandler.setException(new IncorrectPasswordException());
            return;
        }
        loggedIn = true;
        Menu.setCurrentUser(User.getUserByUsername(username));
        Menu.setLoggedIn(true);
        Menu.setCurrentMenu(new ChatMenu());
    }


    @Label(name = "create", description = "create a new user")
    @Label(name = "login", description = "login a user")
    @Label(name = "username", description = "user's username")
    @Label(name = "password", description = "user's password")
    @Label(name = "logout", description = "logout the current user")
    public void userconfigDummyMethodForInstruction() {
    }


    @Command(name = "userconfig")
    private void logout(@Argument(name = "logout") String logout) {
        if (Menu.getCurrentMenu() instanceof UserConfigMenu) {
            CommandHandler.setException(new UserNotLoggedInException());
            return;
        }
        Menu.setLoggedIn(false);
        Menu.setCurrentUser(null);
        Menu.setCurrentMenu(new UserConfigMenu());
    }


    public static void setLoggedIn(boolean loggedIn) {
        UserConfigController.loggedIn = loggedIn;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }
}
