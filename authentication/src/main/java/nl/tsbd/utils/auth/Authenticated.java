package nl.tsbd.utils.auth;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Serviceses with this annotation will be checkeck for authentication.
 */
@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface Authenticated {
    @Nonbinding
    String[] roles() default {};
}
