package main.vaadinui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthResponse;
import main.vaadinui.dto.UserDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.AuthService;
import main.vaadinui.service.UserService;

@Route("login")
@RouteAlias("")
@PageTitle("Вход | Платформа фильмов")
@AnonymousAllowed
@Slf4j
public class LoginView extends VerticalLayout {

    private final TextField username;
    private final PasswordField password;
    private final AuthService authService;
    private final SecurityService securityService;
    private final UserService userService;

    public LoginView(AuthService authService, SecurityService securityService, UserService userService) {
        this.authService = authService;
        this.securityService = securityService;
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Заголовок
        H1 title = new H1("Платформа фильмов");

        // Поля формы
        username = new TextField("Имя пользователя");
        username.setRequired(true);
        username.setWidth("300px");

        password = new PasswordField("Пароль");
        password.setRequired(true);
        password.setWidth("300px");

        // Кнопки
        Button loginButton = new Button("Войти", e -> login());
        loginButton.setWidth("300px");

        Button registerButton = new Button("Регистрация", e ->
                UI.getCurrent().navigate(RegisterView.class));
        registerButton.setWidth("300px");

        // Добавляем компоненты на форму
        add(title, username, password, loginButton, registerButton);
    }

    private void login() {
        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Пожалуйста, заполните все поля")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            log.info("Попытка входа для пользователя: {}", username.getValue());
            AuthResponse response = authService.login(username.getValue(), password.getValue());
            log.info("Вход успешен, токен получен");

            securityService.setCurrentUser(response);
            log.info("Пользователь установлен в SecurityService");

            // Сохраняем в localStorage
            UI.getCurrent().getPage().executeJs(
                    "localStorage.setItem('auth_token', $0);" +
                            "localStorage.setItem('username', $1);" +
                            "localStorage.setItem('user_role', $2);",
                    response.getToken(), response.getUsername(), response.getRole());
            log.info("Данные аутентификации сохранены в localStorage");

            // Получаем реальный ID пользователя
            try {
                UserDto userDto = userService.getUserByUsername(response.getUsername());
                if (userDto != null) {
                    securityService.setUserId(userDto.getId());
                    log.info("Установлен реальный ID пользователя: {}", userDto.getId());
                }
            } catch (Exception e) {
                log.warn("Не удалось получить реальный ID пользователя", e);
            }

            log.info("Переход к MoviesView");
            UI.getCurrent().getPage().executeJs("window.location.href = 'movies';");
        } catch (ApiException e) {
            log.error("Ошибка API при входе: {}", e.getMessage());
            if (e.getStatusCode() == 401) {
                Notification.show("Неверное имя пользователя или пароль")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Notification.show("Ошибка при входе: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            log.error("Неожиданная ошибка при входе", e);
            Notification.show("Произошла ошибка при входе")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}