package main.vaadinui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.MovieProposalDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import main.vaadinui.service.MovieProposalService;

import java.util.List;

@Route(value = "proposals", layout = MainLayout.class)
@PageTitle("Предложения фильмов | Платформа фильмов")
@Slf4j
public class MovieProposalsView extends VerticalLayout {

    private final Grid<MovieProposalDto> grid = new Grid<>(MovieProposalDto.class, false);
    private final MovieProposalService movieProposalService;
    private final SecurityService securityService;

    public MovieProposalsView(MovieProposalService movieProposalService, SecurityService securityService) {
        this.movieProposalService = movieProposalService;
        this.securityService = securityService;

        setSizeFull();

        // Проверка прав доступа
        if (!securityService.isAdmin()) {
            add(new H2("Доступ запрещен"));
            return;
        }

        // Заголовок
        H2 title = new H2("Предложения фильмов");

        // Настраиваем таблицу
        configureGrid();

        // Кнопка добавления предложения
        Button addButton = new Button("Добавить предложение", e -> showProposalForm());

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

        grid.addColumn(MovieProposalDto::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(MovieProposalDto::getUserId).setHeader("ID пользователя").setAutoWidth(true);
        grid.addColumn(MovieProposalDto::getTitle).setHeader("Название").setAutoWidth(true);
        grid.addColumn(MovieProposalDto::getGenre).setHeader("Жанр").setAutoWidth(true);
        grid.addColumn(MovieProposalDto::getDescription).setHeader("Описание").setAutoWidth(true);
        grid.addColumn(MovieProposalDto::getStatus).setHeader("Статус").setAutoWidth(true);

        // Колонки действий
        grid.addComponentColumn(proposal -> {
            Button approveButton = new Button("Принять", e -> approveProposal(proposal));
            if (!"PENDING".equals(proposal.getStatus())) {
                approveButton.setEnabled(false);
            }
            return approveButton;
        }).setHeader("Принять").setAutoWidth(true);

        grid.addComponentColumn(proposal -> {
            Button rejectButton = new Button("Отклонить", e -> rejectProposal(proposal));
            if (!"PENDING".equals(proposal.getStatus())) {
                rejectButton.setEnabled(false);
            }
            return rejectButton;
        }).setHeader("Отклонить").setAutoWidth(true);
    }

    private void refreshGrid() {
        try {
            List<MovieProposalDto> proposals = movieProposalService.getAllProposals();
            grid.setItems(proposals);
        } catch (Exception e) {
            log.error("Ошибка при получении списка предложений", e);
            Notification.show("Не удалось загрузить список предложений: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showProposalForm() {
        MovieProposalFormDialog dialog = new MovieProposalFormDialog(movieProposalService);
        dialog.addListener(MovieProposalFormDialog.SaveEvent.class, event -> refreshGrid());
        dialog.open();
    }

    private void approveProposal(MovieProposalDto proposal) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Подтверждение предложения");

        TextArea commentField = new TextArea("Комментарий администратора");
        commentField.setWidthFull();

        Button confirmButton = new Button("Подтвердить", e -> {
            try {
                movieProposalService.approveProposal(proposal.getId(), commentField.getValue());
                dialog.close();
                refreshGrid();
                Notification.show("Предложение одобрено").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (ApiException ex) {
                Notification.show("Ошибка при одобрении предложения: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                log.error("Ошибка при одобрении предложения", ex);
                Notification.show("Произошла ошибка при одобрении предложения")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(commentField);
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);

        dialog.add(dialogLayout, buttonLayout);
        dialog.open();
    }

    private void rejectProposal(MovieProposalDto proposal) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Отклонение предложения");

        TextArea commentField = new TextArea("Комментарий администратора");
        commentField.setWidthFull();
        commentField.setRequired(true);

        Button confirmButton = new Button("Отклонить", e -> {
            if (commentField.isEmpty()) {
                Notification.show("Пожалуйста, укажите причину отклонения")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                movieProposalService.rejectProposal(proposal.getId(), commentField.getValue());
                dialog.close();
                refreshGrid();
                Notification.show("Предложение отклонено").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (ApiException ex) {
                Notification.show("Ошибка при отклонении предложения: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                log.error("Ошибка при отклонении предложения", ex);
                Notification.show("Произошла ошибка при отклонении предложения")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Отмена", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(commentField);
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);

        dialog.add(dialogLayout, buttonLayout);
        dialog.open();
    }
}