-- Схема для пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     role VARCHAR(20) NOT NULL,
                                     created_at TIMESTAMP NOT NULL,
                                     updated_at TIMESTAMP
);

-- Схема для хранения фильмов пользователей (связь с фильмами из Movie Service)
CREATE TABLE IF NOT EXISTS user_movies (
                                           id BIGSERIAL PRIMARY KEY,
                                           user_id BIGINT NOT NULL,
                                           movie_id BIGINT NOT NULL,
                                           rating INT CHECK (rating >= 1 AND rating <= 5),
                                           note TEXT,
                                           added_to_collection BOOLEAN NOT NULL DEFAULT TRUE,
                                           created_at TIMESTAMP NOT NULL,
                                           updated_at TIMESTAMP,
                                           CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                                           CONSTRAINT uq_user_movie UNIQUE (user_id, movie_id)
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_user_movies_user_id ON user_movies (user_id);
CREATE INDEX idx_user_movies_movie_id ON user_movies (movie_id);
CREATE INDEX idx_user_movies_rating ON user_movies (rating);