PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id        INTEGER PRIMARY KEY,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    role      TEXT NOT NULL CHECK(role IN ('employee', 'manager')),
    full_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id   INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS expenses (
    id           INTEGER PRIMARY KEY,
    user_id      INTEGER NOT NULL REFERENCES users(id),
    category_id  INTEGER NOT NULL REFERENCES categories(id),
    amount       REAL    NOT NULL CHECK(amount > 0 AND amount <= 10000),
    description  TEXT    NOT NULL,
    expense_date TEXT    NOT NULL,  -- format: YYYY-MM-DD
    created_at   TEXT    NOT NULL   -- format: YYYY-MM-DD HH:MM:SS
);

CREATE TABLE IF NOT EXISTS approvals (
    id          INTEGER PRIMARY KEY,
    expense_id  INTEGER NOT NULL UNIQUE REFERENCES expenses(id),
    status      TEXT    NOT NULL DEFAULT 'pending'
                        CHECK(status IN ('pending', 'approved', 'denied')),
    reviewer_id INTEGER REFERENCES users(id),
    comment     TEXT,
    review_date TEXT    -- format: YYYY-MM-DD
);