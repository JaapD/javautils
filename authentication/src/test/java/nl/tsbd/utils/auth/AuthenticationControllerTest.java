package nl.tsbd.utils.auth;

import nl.tsbd.util.ldap.exception.LoginFailedException;
import nl.tsbd.util.testutils.ExtendWithMockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWithMockito
class AuthenticationControllerTest {

    @Mock
    private FacesContext facesContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    ExternalContext externalContext;
    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        PropertiesForAuthentication properties = new PropertiesForAuthentication();
        properties.setProperty("page.after.login", "/pages/main/index.xhtml");
        controller = new AuthenticationController(authenticationService, facesContext, properties);
    }

    @Test
    void loginOk() throws LoginFailedException {
        User user = new User("John");
        user.addRole(new Role("test"));
        mockSession(user);
        when(authenticationService.getUser(eq("John"), any())).thenReturn(user);
        controller.setLoginName("John");

        String result = controller.login();

        assertThat(result).isEqualTo("/pages/main/index.xhtml");
        assertThat(controller.getUser().hasRole("test")).isTrue();
        assertThat(controller.getUser().hasRole("none")).isFalse();
        assertThat(controller.getUser().isAdmin()).isFalse();
        verify(facesContext, never()).addMessage(isNull(), any());
    }

    @Test
    void loginFailed() throws LoginFailedException {
        mockSession(null);
        when(authenticationService.getUser(eq("John"), any())).thenThrow(new LoginFailedException("failed"));
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(externalContext.getResponse()).thenReturn(response);

        controller.setLoginName("John");

        String result = controller.login();

        assertThat(result).isEqualTo("index.jsf");
        assertThat(controller.getUser()).isNull();
        verify(facesContext).addMessage(isNull(), any());
    }

    @Test
    void testIsAuthenticated() {
        mockSession(new User("John"));

        assertThat(controller.isAuthenticated()).isTrue();
    }

    private void mockSession(User user) {
        when(request.getSession()).thenReturn(session);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequest()).thenReturn(request);
        when(session.getAttribute("user")).thenReturn(user);
    }

}
