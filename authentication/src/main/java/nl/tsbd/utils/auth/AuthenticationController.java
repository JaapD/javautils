package nl.tsbd.utils.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@SessionScoped
@Named
@Slf4j
public class AuthenticationController implements Serializable {
    private final String pageAfterLogin;
    private final String pageAfterLogout;
    @Getter
    @Setter
    private String loginName;
    @Getter
    @Setter
    private String password;
    private final AuthenticationService authenticationService;
    private final FacesContext facesContext;

    /**
     * Used by Weld.
     */
    public AuthenticationController() {
        this(null, null, new PropertiesForAuthentication());
    }

    @Inject
    public AuthenticationController(AuthenticationService authenticationService, FacesContext facesContext, PropertiesForAuthentication propertiesForAuthentication) {
        this.authenticationService = authenticationService;
        this.facesContext = facesContext;
        pageAfterLogin = propertiesForAuthentication.getProperty("page.after.login");
        pageAfterLogout = propertiesForAuthentication.getProperty("logout");
    }

    @Produces
    public User getUser() {
        HttpServletRequest request = getRequest();
        User user = (User) request.getSession().getAttribute("user");
        return user;
    }

    public String login() {
        try {
            log.info("Login for {} ", loginName);
            User user = authenticationService.getUser(loginName, password);
            HttpServletRequest request = getRequest();
            request.getSession().setAttribute("user", user);
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Verkeerde naam of wachtwoord", "Verkeerde naam of wachtwoord"));
            getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("Foute inlogpoging voor '{}'", loginName);
            return "index.jsf";
        }
        return pageAfterLogin;
    }

    public String logout() {
        log.info("Logout");
        HttpServletRequest request = getRequest();
        request.getSession().setAttribute("user", null);
        return pageAfterLogout;
    }

    public String getUsername() {
        User user = getUser();
        return user != null ? user.getName() : "-";
    }

    public boolean isAuthenticated() {
        return null != getUser();
    }

    private HttpServletRequest getRequest() {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    private HttpServletResponse getResponse() {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }
}
