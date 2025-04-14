package main.vaadinui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.MovieDto;
import main.vaadinui.dto.UserMovieDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.MovieService;
import main.vaadinui.service.UserMovieService;

import java.util.List;

@Route(value = "my-movies", layout = MainLayout.class)
@PageTitle("Моя коллекция | Платформа фильмов")
@Slf4j
public class MyMoviesView extends VerticalLayout {

    private final Grid<UserMovieDto> grid = new Grid<>(UserMovieDto.class, false);
    private final UserMovieService userMovieService;
    private final MovieService movieService;
    private final SecurityService securityService;

    public MyMoviesView(UserMovieService userMovieService, MovieService movieService, SecurityService securityService) {
        this.userMovieService = userMovieService;
        this.movieService = movieService;
        this.securityService = securityService;

        setSizeFull();

        // Заголовок
        H2 title = new H2("Моя коллекция фильмов");

        // Настраиваем таблицу
        configureGrid();

        // Кнопка добавления фильма
        Button addButton = new Button("Добавить фильм", e -> getUI().ifPresent(ui -> ui.navigate(MoviesView.class)));

        // Составляем панель инструментов
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.setWidthFull();

        // Добавляем компоненты на форму
        add(title, toolbar, grid);

        // Загружаем данные
        refreshGrid();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(UserMovieDto::getMovieTitle).setHeader("Название").setAutoWidth(true);
        grid.addColumn(UserMovieDto::getMovieGenre).setHeader("Жанр").setAutoWidth(true);
        grid.addColumn(UserMovieDto::getRating).setHeader("Моя оценка").setAutoWidth(true);
        grid.addColumn(UserMovieDto::getNote).setHeader("Заметка").setAutoWidth(true);

        // Колонки действий
        grid.addComponentColumn(userMovie -> {
            Button rateButton = new Button("Оценить", e -> showRatingDialog(userMovie));
            return rateButton;
        }).setHeader("Оценить").setAutoWidth(true);

        grid.addComponentColumn(userMovie -> {
            Button removeButton = new Button("Удалить", e -> removeFromCollection(userMovie));
            return removeButton;
        }).setHeader("Удалить").setAutoWidth(true);
    }

    private void refreshGrid() {
        try {
            Long userId = securityService.getCurrentUserId();
            List<UserMovieDto> userMovies = userMovieService.getUserMovies(userId);
            grid.setItems(userMovies);
        } catch (Exception e) {
            log.error("Ошибка при получении коллекции фильмов", e);
            Notification.show("Не удалось загрузить коллекцию фильмов: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showRatingDialog(UserMovieDto userMovie) {
        RatingDialog dialog = new RatingDialog(userMovieService, securityService.getCurrentUserId(), userMovie.getMovieId(), userMovie.getRating());
        dialog.addListener(RatingDialog.SaveEvent.class, event -> refreshGrid());
        dialog.open();
    }

    private void removeFromCollection(UserMovieDto userMovie) {
        try {
            userMovieService.removeMovieFromCollection(securityService.getCurrentUserId(), userMovie.getMovieId());
            refreshGrid();
            Notification.show("Фильм удален из коллекции").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ApiException e) {
            Notification.show("Ошибка при удалении фильма из коллекции: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при удалении фильма из коллекции", e);
            Notification.show("Произошла ошибка при удалении фильма из коллекции")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}