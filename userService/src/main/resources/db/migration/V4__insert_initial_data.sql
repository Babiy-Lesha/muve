-- Вставка начальных фильмов
INSERT INTO movies (title, description, genre, rating, created_at) VALUES
('Побег', 'Оказавшись в тюрьме.', 'Драма', 4.7, CURRENT_TIMESTAMP),
('Крёстный отец', 'Глава семьи.', 'Криминал', 4.4, CURRENT_TIMESTAMP),
('рыцарь', 'преступность.', 'Боевик', 4.5, CURRENT_TIMESTAMP),
('Матрица', 'мир — виртуальный.', 'Фантастика', 4.2, CURRENT_TIMESTAMP),
('Братство', 'уничтожить.', 'Фэнтези', 4.6, CURRENT_TIMESTAMP);

-- Вставка примеров предложений фильмов
INSERT INTO movie_proposals (user_id, title, description, genre, status, created_at) VALUES
(2, 'Зеленая миля', 'начальник блока.', 'Драма', 'PENDING', CURRENT_TIMESTAMP),
(3, 'Начало', 'опытный вор.', 'Фантастика', 'PENDING', CURRENT_TIMESTAMP);