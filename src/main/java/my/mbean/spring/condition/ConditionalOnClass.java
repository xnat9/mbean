package my.mbean.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Created by xnat on 17/5/14.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnClassCondition.class)
public @interface ConditionalOnClass {
    /**
     * The classes that must be present. Since this annotation parsed by loading class
     * bytecode it is safe to specify classes here that may ultimately not be on the
     * classpath.
     * @return the classes that must be present
     */
    Class<?>[] value() default {};

    /**
     * The classes names that must be present.
     * @return the class names that must be present.
     */
    String[] name() default {};
}
