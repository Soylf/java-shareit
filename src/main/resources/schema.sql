CREATE TABLE IF NOT EXISTS users (
      id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      name      VARCHAR(255) NOT NULL,
      email     VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
        id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        owner_id            BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
        name VARCHAR(255) NOT NULL,
        description VARCHAR(512) NOT NULL,
        is_available BOOLEAN
);

CREATE TABLE IF NOT EXISTS bookings (
        id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        start_date TIMESTAMP WITH TIME ZONE NOT NULL,
        end_date TIMESTAMP WITH TIME ZONE NOT NULL,
        item_id             BIGINT REFERENCES items (id) ON DELETE CASCADE NOT NULL,
        booker_id           BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
        status VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
        id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        text VARCHAR(512) NOT NULL,
        item_id         BIGINT REFERENCES items (id) ON DELETE CASCADE NOT NULL,
        author_id       BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
        created TIMESTAMP WITH TIME ZONE NOT NULL
);