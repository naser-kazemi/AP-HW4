package Controller;

import Model.Exceptions.NoSuchCommandExistsException;
import Model.Exceptions.UnsuccessfulException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandHandler {

    private static Exception exception = null;


    private static boolean isItTheRightMethod(Method method, String[] args) {
        int counter = 0;
        if (method.getParameterCount() == 0)
            return false;
        Annotation[][] arguments = method.getParameterAnnotations();

        if (args.length != arguments.length) return false;
        for (String arg : args) {
            String[] parts = arg.split(" ");
            for (Annotation[] argument : arguments) {
                try {
                    if (!((Argument) argument[0]).hasValue() && parts.length > 1) return false;
                    if (((Argument) argument[0]).name().equals(parts[0])) {
                        counter++;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }
        return counter == arguments.length;
    }


    private static LinkedHashMap<String, String> sortArguments(Method method, String[] args) {
        LinkedHashMap<String, String> sortedArguments = new LinkedHashMap<>();
        Annotation[][] arguments = method.getParameterAnnotations();
        for (Annotation[] argument : arguments) {
            for (String arg : args) {
                String[] parts = arg.split(" ");
                if (parts[0].equals(((Argument) argument[0]).name())) {
                    sortedArguments.put(parts[0], arg.replace(parts[0] + " ", ""));
                    break;
                }
            }
        }
        return sortedArguments;
    }


    private static Argument getArgumentByName(Method method, String name) {
        Annotation[][] arguments = method.getParameterAnnotations();
        for (Annotation[] argument : arguments)
            if (((Argument) argument[0]).name().equals(name))
                return (Argument) argument[0];
        return null;
    }


    private static boolean isCommandNameLabel(Object target, String commandName) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods)
            try {
                if (method.getDeclaredAnnotation(Command.class).name().equals(commandName) &&
                        method.getDeclaredAnnotation(Command.class).isLabeled())
                    return true;
            } catch (NullPointerException ignored) {

            }
        return false;
    }


    private static boolean isItTheRightLabeledMethod(Method method, String[] args) {
        int counter = 0;
        Label[] labels = method.getDeclaredAnnotationsByType(Label.class);
        if (labels.length != args.length)
            return false;
        for (String arg : args) {
            String[] parts = arg.split(" ");
            if (parts.length > 1) return false;
            for (Label label : labels) {
                if (label.name().equals(parts[0])) {
                    counter++;
                    break;
                }
            }
        }
        return counter == args.length;
    }


    private static void showInstructionsForCommand(Object target, String commandName)
            throws NoSuchCommandExistsException, UnsuccessfulException {
        try {
            Method method = target.getClass().getDeclaredMethod(commandName + "DummyMethodForInstruction");
            Label[] labels = method.getAnnotationsByType(Label.class);
            System.out.println(commandName + " (help):");
            for (Label label : labels) System.out.println(label.name() + " : " + label.description());
        } catch (NoSuchMethodException ignored) {
            throw new NoSuchCommandExistsException();
        }
        throw new UnsuccessfulException();
    }


    public static void execute(Object target, String command, String commandName) throws Exception {
        String[] args = parse(command);
        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) method.setAccessible(true);
        Method methodToBeInvoked = null;
        if (isCommandNameLabel(target, commandName)) {
            executeLabeledCommand(target, commandName, args, methods, methodToBeInvoked);
        } else {
            executeCommand(target, commandName, args, methods, methodToBeInvoked);
        }

    }

    private static void executeCommand(Object target, String commandName, String[] args, Method[] methods, Method methodToBeInvoked)
            throws Exception {
        ArrayList<String> arguments = new ArrayList<>();
        for (Method method : methods)
            if (isItTheRightMethod(method, args)) {
                methodToBeInvoked = method;
                break;
            }
        if (methodToBeInvoked == null) {
            showInstructionsForCommand(target, commandName);
            return;
        }
        if (methodToBeInvoked.getParameterCount() == 0)
            return;
        LinkedHashMap<String, String> sortedArguments = sortArguments(methodToBeInvoked, args);
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
            if (exception != null) throw exception;
            e.printStackTrace();
        }
    }

    private static void executeLabeledCommand(Object target, String commandName, String[] args, Method[] methods, Method methodToBeInvoked)
            throws Exception {
        for (Method method : methods)
            if (isItTheRightLabeledMethod(method, args)) {
                methodToBeInvoked = method;
                break;
            }
        if (methodToBeInvoked == null) {
            showInstructionsForCommand(target, commandName);
            return;
        }
        try {
            methodToBeInvoked.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (exception != null) throw exception;
            e.printStackTrace();
        }
    }


    private static String[] parse(String command) {
        String[] args = command.split("--");
        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);
        for (int i = 0; i < arguments.length; i++) arguments[i] = arguments[i].trim();
        return arguments;
    }


    public static void setException(Exception exception) {
        CommandHandler.exception = exception;
    }


    public static Exception getException() {
        return exception;
    }
}
