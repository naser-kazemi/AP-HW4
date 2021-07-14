package Controller;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LabelContainer.class)
@Target(ElementType.METHOD)
public @interface Label {
    String name() default "";
    String description() default "";
}
