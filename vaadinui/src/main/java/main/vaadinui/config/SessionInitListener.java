package main.vaadinui.config;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import elemental.json.JsonArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthResponse;
import main.vaadinui.security.SecurityService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionInitListener implements VaadinServiceInitListener {

    private final SecurityService securityService;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (securityService.getCurrentUser() == null) {
                    PendingJavaScriptResult result = uiEvent.getUI().getPage().executeJs(
                            "return [localStorage.getItem('auth_token'), " +
                                    "localStorage.getItem('username'), " +
                                    "localStorage.getItem('user_role')]"
                    );
                    result.then(jsonValue -> {

                        if (jsonValue instanceof JsonArray) {
                            JsonArray jsonArray = (JsonArray) jsonValue;
                            String token = jsonArray.getString(0);
                            String username = jsonArray.getString(1);
                            String role = jsonArray.getString(2);

                            if (token != null && !token.equals("null") && !token.isEmpty()) {

                                AuthResponse authResponse = new AuthResponse();
                                authResponse.setToken(token);
                                authResponse.setUsername(username);
                                authResponse.setRole(role);
                                authResponse.setTokenType("Bearer");

                                securityService.setCurrentUser(authResponse);

                                if (enterEvent.getLocation().getPath().equals("login")) {
                                    enterEvent.forwardTo("movies");
                                }
                            } else if (!enterEvent.getLocation().getPath().equals("login") &&
                                    !enterEvent.getLocation().getPath().equals("register")) {
                                enterEvent.forwardTo("login");
                            }
                        }
                    });
                }
            });
        });
    }
}