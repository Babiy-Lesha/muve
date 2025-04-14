package main.vaadinui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.UserCreateDto;
import main.vaadinui.dto.UserDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.service.AuthService;

@Route("register")
@PageTitle("Регистрация | Платформа фильмов")
@AnonymousAllowed
@Slf4j
public class RegisterView extends VerticalLayout {

    private final TextField username;
    private final PasswordField password;
    private final PasswordField confirmPassword;
    private final EmailField email;
    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Заголовок
        H2 title = new H2("Регистрация нового пользователя");

        // Поля формы
        username = new TextField("Имя пользователя");
        username.setRequired(true);
        username.setWidth("300px");

        password = new PasswordField("Пароль");
        password.setRequired(true);
        password.setWidth("300px");

        confirmPassword = new PasswordField("Подтвердите пароль");
        confirmPassword.setRequired(true);
        confirmPassword.setWidth("300px");

        email = new EmailField("Email");
        email.setRequired(true);
        email.setWidth("300px");

        // Кнопки
        Button registerButton = new Button("Зарегистрироваться", e -> register());
        registerButton.setWidth("300px");

        Button backButton = new Button("Назад", e -> UI.getCurrent().navigate(LoginView.class));
        backButton.setWidth("300px");

        // Добавляем компоненты на форму
        add(title, username, password, confirmPassword, email, registerButton, backButton);
    }

    private void register() {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            Notification.show("Пожалуйста, заполните все поля")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!password.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Пароли не совпадают")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            UserCreateDto userCreateDto = new UserCreateDto(
                    username.getValue(),
                    password.getValue(),
                    email.getValue(),
                    "USER" // По умолчанию роль USER
            );

            UserDto createdUser = authService.register(userCreateDto);

            Dialog successDialog = new Dialog();
            successDialog.setHeaderTitle("Регистрация успешна");

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.add("Пользователь успешно зарегистрирован. Теперь вы можете войти в систему.");

            Button closeButton = new Button("OK", e -> {
                successDialog.close();
                UI.getCurrent().navigate(LoginView.class);
            });

            dialogLayout.add(closeButton);
            successDialog.add(dialogLayout);

            successDialog.open();

        } catch (ApiException e) {
            Notification.show("Ошибка при регистрации: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при регистрации", e);
            Notification.show("Произошла ошибка при регистрации")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}