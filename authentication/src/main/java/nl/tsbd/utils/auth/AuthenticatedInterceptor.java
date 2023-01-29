package nl.tsbd.utils.auth;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Authenticated
@Interceptor
@Slf4j
public class AuthenticatedInterceptor implements Serializable {

    @Inject
    private User user;

    @AroundInvoke
    public Object checkAuthenticattion(InvocationContext ctx) throws Exception {
        log.debug("Intercept {} for user {}", ctx.getTarget(), user);
        Authenticated annotation = getAnnotation(ctx.getTarget().getClass(), ctx.getMethod(), Authenticated.class);
        log.debug("Found annotation {}", annotation);
        validateRoles(annotation);
        return ctx.proceed();
    }

    private void validateRoles(Authenticated annotation) {
        if (annotation != null && annotation.roles().length > 0) {
            log.trace("Rollen vereist: {}", annotation.roles());
            boolean isok = false;
            for (String vereist : annotation.roles()) {
                if (user.hasRole(vereist)) {
                    isok = true;
                }
            }
            if (!isok) {
                throw new UnAuthenticatedException("No suitable roles");
            }
        }
    }

    private static <A extends Annotation> A getAnnotation(Class<?> c, Method m, Class<A> a) {
        return m.isAnnotationPresent(a) ? m.getAnnotation(a)
                : c.isAnnotationPresent(a) ? c.getAnnotation(a) : c.getSuperclass().getAnnotation(a);
    }

}
