package main.vaadinui.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.MovieCreateDto;
import main.vaadinui.dto.MovieDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.service.MovieService;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MovieFormDialog extends Dialog {

    private final MovieService movieService;
    private final TextField title = new TextField("Название");
    private final TextArea description = new TextArea("Описание");
    private final ComboBox<String> genre = new ComboBox<>("Жанр");
    private final MovieDto existingMovie;

    public static class SaveEvent extends ComponentEvent<MovieFormDialog> {
        public SaveEvent(MovieFormDialog source) {
            super(source, false);
        }
    }

    public MovieFormDialog(MovieService movieService, MovieDto movie) {
        this.movieService = movieService;
        this.existingMovie = movie;

        setHeaderTitle(movie == null ? "Добавление фильма" : "Редактирование фильма");

        // Настройка полей
        configureFields();

        // Заполнение данными, если редактирование
        if (movie != null) {
            fillFormFields(movie);
        }

        // Кнопки
        Button saveButton = new Button("Сохранить", e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> close());

        // Компоновка
        VerticalLayout formLayout = new VerticalLayout(title, description, genre);
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setPadding(true);

        add(formLayout, buttonLayout);
    }

    private void configureFields() {
        title.setRequired(true);
        title.setWidthFull();

        description.setWidthFull();
        description.setMinHeight("150px");

        List<String> genres = Arrays.asList(
                "Боевик", "Комедия", "Драма", "Фантастика", "Ужасы",
                "Триллер", "Приключения", "Мультфильм", "Документальный", "Криминал"
        );
        genre.setItems(genres);
        genre.setRequired(true);
        genre.setWidthFull();
    }

    private void fillFormFields(MovieDto movie) {
        title.setValue(movie.getTitle() != null ? movie.getTitle() : "");
        description.setValue(movie.getDescription() != null ? movie.getDescription() : "");
        genre.setValue(movie.getGenre() != null ? movie.getGenre() : "");
    }

    private void save() {
        if (!validateForm()) {
            return;
        }

        try {
            if (existingMovie == null) {
                // Создание нового фильма
                MovieCreateDto newMovie = new MovieCreateDto(
                        title.getValue(),
                        description.getValue(),
                        genre.getValue()
                );

                movieService.createMovie(newMovie);
                Notification.show("Фильм успешно добавлен")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                // Обновление существующего фильма
                MovieDto updatedMovie = new MovieDto(
                        existingMovie.getId(),
                        title.getValue(),
                        description.getValue(),
                        genre.getValue(),
                        existingMovie.getRating()
                );

                movieService.updateMovie(existingMovie.getId(), updatedMovie);
                Notification.show("Фильм успешно обновлен")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }

            fireEvent(new SaveEvent(this));
            close();
        } catch (ApiException e) {
            Notification.show("Ошибка при сохранении фильма: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при сохранении фильма", e);
            Notification.show("Произошла ошибка при сохранении фильма")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private boolean validateForm() {
        if (title.isEmpty()) {
            Notification.show("Название фильма обязательно")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        if (genre.isEmpty()) {
            Notification.show("Жанр фильма обязателен")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }

        return true;
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}