CREATE TABLE IF NOT EXISTS users (
  id LONG GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id LONG GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    userId LONG,
    name VARCHAR(255),
    description VARCHAR(255),
    is_available BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id LONG PRIMARY KEY,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    item_id LONG NOT NULL,
    booker_id LONG NOT NULL,
    status VARCHAR(25),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT check_bookings_status CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);
CREATE TABLE IF NOT EXISTS comments (
    id LONG PRIMARY KEY,
    text VARCHAR(512) NOT NULL,
    item_id LONG NOT NULL,
    author_id LONG NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);