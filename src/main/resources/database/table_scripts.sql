CREATE TABLE tables
(
    id          SERIAL PRIMARY KEY NOT NULL,
    poker_type  VARCHAR(25)        NOT NULL,
    small_blind NUMERIC(10, 2)     NOT NULL,
    speaker     VARCHAR(25)        NOT NULL
);

CREATE TABLE balances
(
    id   SERIAL PRIMARY KEY,
    cash NUMERIC(10, 2),
    bet  NUMERIC(10, 2)
);

CREATE TABLE players
(
    id         SERIAL PRIMARY KEY NOT NULL,
    name       VARCHAR(25)        NOT NULL,
    balance_id SERIAL             NOT NULL,
    table_id   SERIAL             NOT NULL,
    constraint p_balance_id_to_b_id foreign key (balance_id) references balances (id),
    constraint p_table_id_to_t_id foreign key (table_id) references tables (id)
);

CREATE TABLE player_states
(
    id            SERIAL PRIMARY KEY NOT NULL,
    player_id     SERIAL             NOT NULL,
    table_id      SERIAL             NOT NULL,
    in_game_state VARCHAR(15)        NOT NULL,
    round_role    VARCHAR(15)        NOT NULL,
    constraint ps_player_id_to_p_id foreign key (player_id) references players (id)
);



