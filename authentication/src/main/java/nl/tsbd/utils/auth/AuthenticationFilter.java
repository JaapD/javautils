package nl.tsbd.utils.auth;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@WebFilter(value = "*")
public class AuthenticationFilter implements Filter {
    private List<String> allowedAll;
    private String login;

    private PropertiesForAuthentication propertiesForAuthentication;

    public AuthenticationFilter() {
        log.info("In de default constructor");
        //needed for weld
    }

    @Inject
    public AuthenticationFilter(PropertiesForAuthentication propertiesForAuthentication) {
        this.propertiesForAuthentication = propertiesForAuthentication;
    }

    @PostConstruct
    public void init() {
        login = propertiesForAuthentication.getProperty("login");
        String logout = propertiesForAuthentication.getProperty("logout");
        String[] split = propertiesForAuthentication.getProperty("allowed.all").split(";");
        allowedAll = new ArrayList<>(List.of(split));
        allowedAll.add(login);
        allowedAll.add(logout);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.trace("Name is: {}", request.getLocalName());
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        User user = (User) httpRequest.getSession().getAttribute("user");
        if (user != null) {
            log.trace("Logged in as {}, can proceed", user);
        chain.doFilter(request, response);
        } else {
            String path = httpRequest.getServletPath();
            if (canProceed(path)) {
                log.trace("Not logged in, but free to go: '{}'", path);
                chain.doFilter(request, response);
            } else {
                log.info("Not logged in and forbidden: '{}'", path);
                HttpServletResponse r = (HttpServletResponse) response;
                r.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization shall be provided");
//                r.sendRedirect(httpRequest.getContextPath()+login);
            }
        }
    }

    @Override
    public void destroy() {
        log.info("Destroy the AthenticationFilter");
    }


    boolean canProceed(String servletPath) {
        for (String ok : allowedAll) {
            Pattern pattern = Pattern.compile(ok);
            if (pattern.matcher(servletPath).matches()) {
                return true;
            }
        }
        return false;
    }
}
