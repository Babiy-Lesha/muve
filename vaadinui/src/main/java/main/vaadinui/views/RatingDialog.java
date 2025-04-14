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
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.exception.ApiException;
import main.vaadinui.service.UserMovieService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RatingDialog extends Dialog {

    private final UserMovieService userMovieService;
    private final Long userId;
    private final Long movieId;
    private final ComboBox<Integer> ratingField = new ComboBox<>("Ваша оценка");

    public static class SaveEvent extends ComponentEvent<RatingDialog> {
        public SaveEvent(RatingDialog source) {
            super(source, false);
        }
    }

    public RatingDialog(UserMovieService userMovieService, Long userId, Long movieId, Integer currentRating) {
        this.userMovieService = userMovieService;
        this.userId = userId;
        this.movieId = movieId;

        setHeaderTitle("Оценка фильма");

        // Настройка поля оценки
        List<Integer> ratings = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        ratingField.setItems(ratings);
        ratingField.setRequired(true);
        ratingField.setWidth("200px");

        if (currentRating != null) {
            ratingField.setValue(currentRating);
        }

        // Кнопки
        Button saveButton = new Button("Сохранить", e -> saveRating());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> close());

        // Компоновка
        VerticalLayout formLayout = new VerticalLayout(ratingField);
        formLayout.setPadding(true);
        formLayout.setSpacing(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setPadding(true);

        add(formLayout, buttonLayout);
    }

    private void saveRating() {
        if (ratingField.isEmpty()) {
            Notification.show("Пожалуйста, выберите оценку")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            userMovieService.rateMovie(userId, movieId, ratingField.getValue());

            Notification.show("Оценка успешно сохранена")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            fireEvent(new SaveEvent(this));
            close();
        } catch (ApiException e) {
            Notification.show("Ошибка при сохранении оценки: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при сохранении оценки", e);
            Notification.show("Произошла ошибка при сохранении оценки")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}