CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(64)
);

CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    description VARCHAR NOT NULL,
    ingredients VARCHAR NOT NULL,
    nutrients VARCHAR,
    servings SMALLINT,
    prep_time SMALLINT,
    cook_time SMALLINT,
    is_budget BOOLEAN NOT NULL
);

CREATE TABLE saved_recipes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    recipe_id BIGINT NOT NULL REFERENCES recipes (id) ON DELETE CASCADE,
    CONSTRAINT unique_user_recipe UNIQUE (user_id, recipe_id)
);

CREATE TABLE following (
    follower_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    following_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

CREATE TABLE ratings (
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    recipe_id BIGINT REFERENCES recipes (id) ON DELETE CASCADE,
    rating SMALLINT
    CHECK (rating BETWEEN 1 AND 5),
    PRIMARY KEY (user_id, recipe_id)
);
