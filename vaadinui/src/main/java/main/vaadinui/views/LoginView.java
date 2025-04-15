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
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.AuthService;

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

    public LoginView(AuthService authService, SecurityService securityService) {
        this.authService = authService;
        this.securityService = securityService;

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

            log.info("Успешный вход, устанавливаем пользователя в SecurityService");
            securityService.setCurrentUser(response);

            log.info("Переход к MoviesView");

            // Используем JavaScript для перехода и перезагрузки страницы
            UI.getCurrent().getPage().executeJs("window.location.href = 'movies';");
        } catch (ApiException e) {
            if (e.getStatusCode() == 401) {
                Notification.show("Неверное имя пользователя или пароль")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Notification.show("Ошибка при входе: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            log.error("Ошибка при авторизации", e);
            Notification.show("Произошла ошибка при входе")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}