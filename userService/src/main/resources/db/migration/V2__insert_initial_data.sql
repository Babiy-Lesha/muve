-- Вставка пользователей
INSERT INTO users (username, password, email, role, created_at) VALUES
-- Пароль: admin123
('admin', '$2a$10$QaoMyQTCFtH2GVj/tDjCX.fWJgxu.y4HSW/fsuJnZGJw8U3LHMDcC', 'admin@movies.com', 'ADMIN', CURRENT_TIMESTAMP),
-- Пароль: user123
('user1', '$2a$10$8dNIYCFitoZBQCUePUfyae/FUg8xw7dKuPURlW7gLOSEM/BQDX6h2', 'user1@example.com', 'USER', CURRENT_TIMESTAMP),
-- Пароль: user123
('user2', '$2a$10$8dNIYCFitoZBQCUePUfyae/FUg8xw7dKuPURlW7gLOSEM/BQDX6h2', 'user2@example.com', 'USER', CURRENT_TIMESTAMP);

-- Записи фильмов в коллекциях пользователей (movie_id должны соответствовать ID в Movie Service)
INSERT INTO user_movies (user_id, movie_id, rating, note, added_to_collection, created_at) VALUES
                                                                                               (2, 1, 5, 'Мой любимый фильм', true, CURRENT_TIMESTAMP),
                                                                                               (2, 2, 4, 'Отличный классический фильм', true, CURRENT_TIMESTAMP),
                                                                                               (3, 3, 3, 'Интересный, но не шедевр', true, CURRENT_TIMESTAMP);