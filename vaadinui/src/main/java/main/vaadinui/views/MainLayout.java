package main.vaadinui.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import main.vaadinui.security.SecurityService;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
    }

    private void createHeader() {
        H2 logo = new H2("Платформа фильмов");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        Button moviesButton = new Button("Все фильмы", e -> getUI().ifPresent(ui -> ui.navigate("movies")));
        Button myMoviesButton = new Button("Мои фильмы", e -> getUI().ifPresent(ui -> ui.navigate("my-movies")));
        Button proposalsButton = new Button("Предложенные фильмы", e -> getUI().ifPresent(ui -> ui.navigate("proposals")));
        Button usersButton = new Button("Пользователи", e -> getUI().ifPresent(ui -> ui.navigate("users")));
        Button logoutButton = new Button("Выйти", e -> {
            securityService.setCurrentUser(null);
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        HorizontalLayout navButtons = new HorizontalLayout(moviesButton, myMoviesButton, proposalsButton, usersButton, logoutButton);
        navButtons.setWidthFull();
        navButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        navButtons.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout headerContent = new HorizontalLayout(logo, navButtons);
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);

        addToNavbar(headerContent);
    }
}