package main.vaadinui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.security.SecurityService;

@Slf4j
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        log.info("MainLayout создан с пользователем: {}",
                securityService.getCurrentUser() != null ? securityService.getCurrentUser().getUsername() : "null");
    }

    private void createHeader() {
        H2 logo = new H2("Платформа фильмов");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        Header header;

        if (securityService.getCurrentUser() != null) {
            log.info("Создание навигации для пользователя: {}", securityService.getCurrentUser().getUsername());

            // Навигационные кнопки
            Button moviesButton = new Button("Все фильмы", e ->
                    getUI().ifPresent(ui -> ui.navigate(MoviesView.class)));

            Button myMoviesButton = new Button("Мои фильмы", e ->
                    getUI().ifPresent(ui -> ui.navigate(MyMoviesView.class)));

            // Контейнер для кнопок навигации
            HorizontalLayout navButtons = new HorizontalLayout(moviesButton, myMoviesButton);

            if (securityService.isAdmin()) {
                log.info("Добавление кнопок администратора");

                Button proposalsButton = new Button("Предложенные фильмы", e ->
                        getUI().ifPresent(ui -> ui.navigate(MovieProposalsView.class)));

                Button usersButton = new Button("Пользователи", e ->
                        getUI().ifPresent(ui -> ui.navigate(UsersView.class)));

                navButtons.add(proposalsButton, usersButton);
            }

            // Кнопка выхода
            Button logoutButton = new Button("Выйти", e -> {
                securityService.logout();
            });

            // Создаем горизонтальный макет с логотипом, кнопками навигации и кнопкой выхода
            HorizontalLayout headerContent = new HorizontalLayout(logo, navButtons, logoutButton);
            headerContent.setWidthFull();
            headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            headerContent.setAlignItems(FlexComponent.Alignment.CENTER);

            header = new Header(headerContent);
        } else {
            log.warn("Пользователь не авторизован, шапка без навигации");
            header = new Header(logo);
        }

        addToNavbar(header);
    }
}