package main.vaadinui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.security.SecurityService;

@Slf4j
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
    }

    private void createHeader() {
        H2 logo = new H2("Платформа фильмов");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        var header = new Header(logo);

        if (securityService.getCurrentUser() != null) {
            // Навигационные кнопки
            Button moviesButton = new Button("Все фильмы", e ->
                    getUI().ifPresent(ui -> ui.navigate(MoviesView.class)));

            Button myMoviesButton = new Button("Мои фильмы", e ->
                    getUI().ifPresent(ui -> ui.navigate(MyMoviesView.class)));

            // Кнопки для админа
            HorizontalLayout navButtons = new HorizontalLayout(moviesButton, myMoviesButton);

            if (securityService.isAdmin()) {
                Button proposalsButton = new Button("Предложенные фильмы", e ->
                        getUI().ifPresent(ui -> ui.navigate(MovieProposalsView.class)));

                Button usersButton = new Button("Пользователи", e ->
                        getUI().ifPresent(ui -> ui.navigate(UsersView.class)));

                navButtons.add(proposalsButton, usersButton);
            }

            // Кнопка выхода
            Button logoutButton = new Button("Выйти", e -> {
                securityService.setCurrentUser(null);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            });

            HorizontalLayout headerContent = new HorizontalLayout(logo, navButtons, logoutButton);
            headerContent.setWidthFull();
            headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            headerContent.setAlignItems(FlexComponent.Alignment.CENTER);

            header = new Header(headerContent);
        }

        addToNavbar(header);
    }
}