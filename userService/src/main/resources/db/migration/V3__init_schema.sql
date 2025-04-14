-- Схема для фильмов
CREATE TABLE IF NOT EXISTS movies (
                                      id BIGSERIAL PRIMARY KEY,
                                      title VARCHAR(255) NOT NULL,
                                      description TEXT,
                                      genre VARCHAR(50),
                                      rating DECIMAL(3, 1) DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
                                      created_at TIMESTAMP NOT NULL,
                                      updated_at TIMESTAMP
);

-- Схема для предложений фильмов
CREATE TABLE IF NOT EXISTS movie_proposals (
                                               id BIGSERIAL PRIMARY KEY,
                                               user_id BIGINT NOT NULL,
                                               title VARCHAR(255) NOT NULL,
                                               description TEXT,
                                               genre VARCHAR(50),
                                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                               created_at TIMESTAMP NOT NULL,
                                               updated_at TIMESTAMP,
                                               reviewed_at TIMESTAMP,
                                               admin_comment TEXT
);

ALTER TABLE movies ALTER COLUMN rating TYPE DOUBLE PRECISION;
-- Индексы для оптимизации запросов
CREATE INDEX idx_movies_title ON movies (title);
CREATE INDEX idx_movies_genre ON movies (genre);
CREATE INDEX idx_movie_proposals_status ON movie_proposals (status);
CREATE INDEX idx_movie_proposals_user_id ON movie_proposals (user_id);