package model;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class Agent {

    public List<String> getMethodNames(Object object) {
        Class<?> objectClass = object.getClass();

        Method[] methods = objectClass.getDeclaredMethods();

        return Arrays.stream(methods).map(Method::getName).collect(Collectors.toList());
    }


    public Object getFieldContent(Object object, String fieldName) throws Exception {
        Class<?> objectClass = object.getClass();
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(object);
        field.setAccessible(false);
        return fieldValue;
    }


    public void setFieldContent(Object object, String fieldName, Object content) throws Exception {
        Class<?> objectClass = object.getClass();
        Field field = objectClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        try {
            int fieldModifier = field.getModifiers();
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(object, content);
            modifiersField.setInt(field, fieldModifier);
        } catch (IllegalArgumentException ignored) {

        }
        field.setAccessible(false);
    }


    public Object call(Object object, String methodName, Object[] parameters) throws Exception {
        Class<?> objectClass = object.getClass();
        Class<?>[] parameterClasses = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++)
            parameterClasses[i] = parameters[i].getClass();
        Method method = objectClass.getDeclaredMethod(methodName, parameterClasses);
        method.setAccessible(true);
        Object result = method.invoke(object, parameters);
        method.setAccessible(false);
        return result;
    }


    public Object createANewObject(String fullClassName, Object[] initials) throws Exception {
        Class<?> clazz = Class.forName(fullClassName);
        Class<?>[] initialsClasses = new Class[initials.length];
        for (int i = 0; i < initials.length; i++) {
            initialsClasses[i] = initials[i].getClass();
        }
        Constructor<?> constructor = clazz.getDeclaredConstructor(initialsClasses);
        constructor.setAccessible(true);
        Object instance = constructor.newInstance(initials);
        constructor.setAccessible(false);
        return instance;
    }


    private String getModifierName(Field field) {
        if (Modifier.isPublic(field.getModifiers()))
            return "public";
        if (Modifier.isPrivate(field.getModifiers()))
            return "private";
        if (Modifier.isProtected(field.getModifiers()))
            return "protected";
        return "";
    }

    private String getStaticState(Field field) {
        if (Modifier.isStatic(field.getModifiers()))
            return " static";
        return "";
    }

    private void setClassMethodsInfo(StringBuilder information, Class<?> objectClass) {
        Method[] declaredMethods = objectClass.getDeclaredMethods();
        List<Method> methods = new ArrayList<>();
        Collections.addAll(methods, declaredMethods);
        methods.sort(Comparator.comparing(Method::getName));
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            StringBuilder signature = new StringBuilder(method.getName() + "(");
            for (Parameter parameter : parameters) {
                signature.append(parameter.getType().getSimpleName()).append(", ");
            }
            if (parameters.length != 0)
                signature.replace(signature.length() - 2, signature.length(), ")");
            else
                signature.append(")");
            information.append(method.getReturnType().getSimpleName()).append(" ").append(signature).append(System.lineSeparator());
        }
        information.append("(").append(declaredMethods.length).append(" methods)");
    }

    private void setClassFieldsInfo(StringBuilder information, Class<?> objectClass) {
        Field[] declaredFields = objectClass.getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, declaredFields);
        fields.sort(Comparator.comparing(Field::getName));
        for (Field field : fields)
            information.append(getModifierName(field)).append(getStaticState(field)).append(" ")
                    .append(field.getType().getSimpleName()).append(" ").append(field.getName())
                    .append(System.lineSeparator());
        information.append("(").append(declaredFields.length).append(" fields)").append(System.lineSeparator());
        information.append("===").append(System.lineSeparator());
        information.append("Methods:").append(System.lineSeparator());
    }

    private void setClassInfo(StringBuilder information, Class<?> objectClass) {
        information.append("Name: ").append(objectClass.getSimpleName()).append(System.lineSeparator());
        information.append("Package: ").append(objectClass.getPackage().getName()).append(System.lineSeparator());
        information.append("No. of Constructors: ").append(objectClass.getDeclaredConstructors().length).append(System.lineSeparator());
        information.append("===").append(System.lineSeparator());
        information.append("Fields:").append(System.lineSeparator());
    }

    public String debrief(Object object) {
        StringBuilder information = new StringBuilder();
        Class<?> objectClass = object.getClass();
        setClassInfo(information, objectClass);
        setClassFieldsInfo(information, objectClass);
        setClassMethodsInfo(information, objectClass);
        return information.toString();
    }


    private void cloneObject(Object toClone, Object clone, Field field) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.PRIVATE & ~Modifier.FINAL);
        field.setAccessible(true);
        if (field.get(toClone) == null)
            return;
        if (field.getType().isPrimitive() || field.getType().equals(Boolean.class)
                || field.getType().equals(String.class) || field.get(toClone) instanceof Enum<?>)
            field.set(clone, field.get(toClone));
        else if (field.getType().getSuperclass() == null)
            field.set(clone, clone(field.get(toClone)));
        else if (field.getType().getSuperclass().equals(Number.class))
            field.set(clone, field.get(toClone));
        else {
            if (field.getType().isArray()) {
                int arraySize = Array.getLength(field.get(toClone));
                Class<?> componentType = field.getType().getComponentType();
                Object array = Array.newInstance(componentType, arraySize);
                for (int i = 0; i < arraySize; i++) {
                    Object element = Array.get(field.get(toClone), i);
                    if (componentType.isPrimitive() || componentType.equals(Boolean.class)
                            || componentType.equals(String.class) || element instanceof Enum<?>)
                        Array.set(array, i, element);
                    else Array.set(array, i, clone(element));
                }
                field.set(clone, array);
            } else field.set(clone, clone(field.get(toClone)));
        }
    }


    public Object clone(Object toClone) throws Exception {
        Constructor<?> constructor = toClone.getClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        Object clone = constructor.newInstance();

        for (Field field : toClone.getClass().getDeclaredFields()) {
            cloneObject(toClone, clone, field);
        }

        Class<?> clazz = toClone.getClass();
        while (clazz.getSuperclass() != null) {
            for (Field field : toClone.getClass().getSuperclass().getDeclaredFields()) {
                cloneObject(toClone, clone, field);
            }
            clazz = clazz.getSuperclass();
        }

        return clone;

    }

}
