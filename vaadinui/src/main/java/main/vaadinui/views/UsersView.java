package main.vaadinui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.UserDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Пользователи | Платформа фильмов")
@Slf4j
public class UsersView extends VerticalLayout {

    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);
    private final UserService userService;
    private final SecurityService securityService;

    public UsersView(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
        setSizeFull();

        // Проверка прав доступа
        if (!securityService.isAdmin()) {
            add(new H2("Доступ запрещен"));
            return;
        }

        // Заголовок
        H2 title = new H2("Список пользователей");

        // Настраиваем таблицу
        configureGrid();

        // Добавляем компоненты на форму
        add(title, grid);

        // Загружаем данные
        refreshGrid();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(UserDto::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(UserDto::getUsername).setHeader("Имя пользователя").setAutoWidth(true);
        grid.addColumn(UserDto::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(UserDto::getRole).setHeader("Роль").setAutoWidth(true);
        grid.addColumn(user -> {
            if (user.getCreatedAt() != null) {
                return user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
            return "";
        }).setHeader("Дата регистрации").setAutoWidth(true);

        grid.addComponentColumn(user -> {
            Button deleteButton = new Button("Удалить", e -> deleteUser(user));
            // Не даем удалить самого себя или другого админа
            if (user.getUsername().equals(securityService.getCurrentUser().getUsername()) ||
                    "ADMIN".equals(user.getRole())) {
                deleteButton.setEnabled(false);
            }
            return deleteButton;
        }).setHeader("Действия").setAutoWidth(true);
    }

    private void refreshGrid() {
        try {
            List<UserDto> users = userService.getAllUsers();
            grid.setItems(users);
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            Notification.show("Не удалось загрузить список пользователей: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteUser(UserDto user) {
        try {
            userService.deleteUser(user.getId());
            refreshGrid();
            Notification.show("Пользователь удален").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ApiException e) {
            Notification.show("Ошибка при удалении пользователя: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя", e);
            Notification.show("Произошла ошибка при удалении пользователя")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}