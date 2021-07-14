package Controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    private static boolean isItTheRightMethod(Method method, String[] args) {
        int counter = 0;
        Argument[] arguments = (Argument[]) method.getAnnotations();
        if (args.length != arguments.length)
            return false;
        for (String arg : args) {
            String[] parts = arg.split(" ");
            for (Argument argument : arguments) {
                if (argument.name().equals(parts[0])) {
                    counter++;
                    break;
                }
            }
        }
        return counter == arguments.length;
    }


    private static HashMap<String, String> sortArguments(Method method, String[] args) {
        HashMap<String, String> sortedArguments = new HashMap<>();
        Argument[] arguments = (Argument[]) method.getAnnotations();
        for (Argument argument : arguments) {
            for (String arg : args) {
                String[] parts = arg.split(" ");
                if (parts[0].equals(argument.name())) {
                    sortedArguments.put(parts[0], arg.replace(parts[0] + " ", ""));
                    break;
                }
            }
        }
        return sortedArguments;
    }



    private static Argument getArgumentByName(Method method, String name) {
        Argument[] arguments = (Argument[]) method.getAnnotations();
        for (Argument argument : arguments)
            if (argument.name().equals(name))
                return argument;
        return null;
    }



    public static void parse(Object target, String[] args) {
        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        Method methodToBeInvoked = null;
        ArrayList<String> arguments = new ArrayList<>();
        for (Method method : methods)
            if (isItTheRightMethod(method, args)) {
                methodToBeInvoked = method;
                break;
            }
        if (methodToBeInvoked == null) {
            //TODO show help
            return;
        }
        HashMap<String, String> sortedArguments = sortArguments(methodToBeInvoked, args);
        for (String name : sortedArguments.keySet()) {
            Argument argument = getArgumentByName(methodToBeInvoked, name);
            assert argument != null;
            if (argument.hasValue())
                arguments.add(sortedArguments.get(name));
            else
                arguments.add(name);
        }
        String[] finalArgs = arguments.toArray(new String[0]);
        try {
            methodToBeInvoked.invoke(target, (Object[]) finalArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
