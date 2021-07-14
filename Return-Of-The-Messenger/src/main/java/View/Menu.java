
package View;

import Model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Menu {

    private static Menu currentMenu;
    private static boolean loggedIn = false;
    private static User currentUser;


    protected String controllerMessage;
    protected String lastControllerMessage;
    protected Pattern[] commandPatterns;

    public abstract Pattern[] setCommands();

    public static Matcher getMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }


    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        Menu.currentMenu = currentMenu;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Menu.loggedIn = loggedIn;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Menu.currentUser = currentUser;
    }
}
