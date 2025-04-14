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
import main.vaadinui.dto.MovieProposalDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.service.MovieProposalService;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MovieProposalFormDialog extends Dialog {

    private final MovieProposalService movieProposalService;
    private final TextField title = new TextField("Название");
    private final TextArea description = new TextArea("Описание");
    private final ComboBox<String> genre = new ComboBox<>("Жанр");

    public static class SaveEvent extends ComponentEvent<MovieProposalFormDialog> {
        public SaveEvent(MovieProposalFormDialog source) {
            super(source, false);
        }
    }

    public MovieProposalFormDialog(MovieProposalService movieProposalService) {
        this.movieProposalService = movieProposalService;

        setHeaderTitle("Предложить новый фильм");

        // Настройка полей
        configureFields();

        // Кнопки
        Button saveButton = new Button("Отправить", e -> save());
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

    private void save() {
        if (!validateForm()) {
            return;
        }

        try {
            MovieProposalDto proposal = new MovieProposalDto();
            proposal.setTitle(title.getValue());
            proposal.setDescription(description.getValue());
            proposal.setGenre(genre.getValue());

            movieProposalService.createProposal(proposal);

            Notification.show("Предложение успешно отправлено")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            fireEvent(new SaveEvent(this));
            close();
        } catch (ApiException e) {
            Notification.show("Ошибка при отправке предложения: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Ошибка при отправке предложения", e);
            Notification.show("Произошла ошибка при отправке предложения")
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