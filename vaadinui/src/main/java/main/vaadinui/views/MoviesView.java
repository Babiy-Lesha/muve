package main.vaadinui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import main.vaadinui.dto.MovieDto;
import main.vaadinui.dto.UserMovieDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.MovieService;
import main.vaadinui.service.UserMovieService;

import java.util.List;

@Route(value = "movies", layout = MainLayout.class)
@PageTitle("Фильмы | Платформа фильмов")
@AnonymousAllowed
public class MoviesView extends VerticalLayout {

    private final Grid<MovieDto> grid = new Grid<>(MovieDto.class, false);
    private final MovieService movieService;
    private final UserMovieService userMovieService;
    private final SecurityService securityService;

    private TextField searchField;

    public MoviesView(MovieService movieService, UserMovieService userMovieService, SecurityService securityService) {
        this.movieService = movieService;
        this.userMovieService = userMovieService;
        this.securityService = securityService;

        setSizeFull();

        // Заголовок
        H2 title = new H2("Список всех фильмов");

        // Настраиваем таблицу
        configureGrid();

        // Поисковое поле
        searchField = new TextField();
        searchField.setPlaceholder("Поиск по названию...");
        searchField.setWidth("300px");

        Button searchButton = new Button("Искать", e -> searchMovies());

        // Кнопка добавления фильма (только для админов)
        Button addButton = new Button("Добавить фильм", e -> showMovieForm(null));
        if (!securityService.isAdmin()) {
            addButton.setVisible(false);
        }

        // Составляем панель инструментов
        HorizontalLayout toolbar = new HorizontalLayout(searchField, searchButton, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Добавляем компоненты на форму
        add(title, toolbar, grid);

        // Загружаем данные
        refreshGrid();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addColumn(MovieDto::getId).setHeader("ID").setAutoWidth(true).setSortable(true);
        grid.addColumn(MovieDto::getTitle).setHeader("Название").setAutoWidth(true).setSortable(true);
        grid.addColumn(MovieDto::getGenre).setHeader("Жанр").setAutoWidth(true).setSortable(true);
        grid.addColumn(MovieDto::getDescription).setHeader("Описание").setAutoWidth(true);
        grid.addColumn(MovieDto::getRating).setHeader("Рейтинг").setAutoWidth(true).setSortable(true);

        // Колонка добавления в мою коллекцию
        grid.addComponentColumn(movie -> {
            Button addToCollectionButton = new Button("В коллекцию", e -> addToCollection(movie));
            return addToCollectionButton;
        }).setHeader("Действия").setAutoWidth(true);

        // Колонки для админов
        if (securityService.isAdmin()) {
            grid.addComponentColumn(movie -> {
                Button editButton = new Button("Изменить", e -> showMovieForm(movie));
                return editButton;
            }).setAutoWidth(true);

            grid.addComponentColumn(movie -> {
                Button deleteButton = new Button("Удалить", e -> deleteMovie(movie));
                return deleteButton;
            }).setAutoWidth(true);
        }
    }

    private void refreshGrid() {
        try {
            List<MovieDto> movies = movieService.getAllMovies();
            grid.setItems(movies);
        } catch (Exception e) {
            Notification.show("Не удалось загрузить список фильмов: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void searchMovies() {
        try {
            String searchTerm = searchField.getValue();
            List<MovieDto> movies;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                movies = movieService.searchMovies(searchTerm);
            } else {
                movies = movieService.getAllMovies();
            }

            grid.setItems(movies);
        } catch (Exception e) {
            Notification.show("Не удалось выполнить поиск: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showMovieForm(MovieDto movieDto) {
        MovieFormDialog dialog = new MovieFormDialog(movieService, movieDto);
        dialog.addListener(MovieFormDialog.SaveEvent.class, event -> refreshGrid());
        dialog.open();
    }

    private void deleteMovie(MovieDto movie) {
        try {
            movieService.deleteMovie(movie.getId());
            refreshGrid();
            Notification.show("Фильм удален").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ApiException e) {
            Notification.show("Ошибка при удалении фильма: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Произошла ошибка при удалении фильма")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void addToCollection(MovieDto movie) {
        try {
            Long id = securityService.getCurrentUser().getId();
            UserMovieDto userMovie = userMovieService.addMovieToCollection(id, movie.getId(), null);
            Notification.show("Фильм добавлен в вашу коллекцию")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ApiException e) {
            Notification.show("Ошибка при добавлении фильма в коллекцию: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Произошла ошибка при добавлении фильма в коллекцию")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}