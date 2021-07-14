package Controller;

import View.ChatMenu;
import View.Menu;
import View.UserConfigMenu;

import java.util.Scanner;

public class ProgramRunner {

    public static Thread serverThread;


    public void run() {

        serverThread = new Thread(() -> {
            try {
                ServerController.getInstance().runServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        UserConfigMenu userConfigMenu = new UserConfigMenu();
        ChatMenu chatMenu = new ChatMenu();

        Scanner input = new Scanner(System.in);
        Menu.setCurrentMenu(userConfigMenu);
        while (true) {
            if (Menu.getCurrentMenu() instanceof UserConfigMenu)
                userConfigMenu.run(input);
            else if (Menu.getCurrentMenu() instanceof ChatMenu)
                chatMenu.run(input);
        }
    }


}
