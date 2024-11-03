CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE tables
(
    id          uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    round       INT                                         NOT NULL,
    poker_type  VARCHAR(25)                                 NOT NULL,
    small_blind NUMERIC(10, 2)                              NOT NULL,
    speaker     VARCHAR(25)                                 NOT NULL,
    after_last  VARCHAR(25)                                 NOT NULL
);

CREATE TABLE balances
(
    id   uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    cash NUMERIC(10, 2),
    bet  NUMERIC(10, 2)
);

CREATE TABLE players
(
    id         uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    name       VARCHAR(25)                                 NOT NULL,
    balance_id uuid                                        NOT NULL,
    table_id   uuid                                        NOT NULL,
    constraint p_balance_id_to_b_id foreign key (balance_id) references balances (id),
    constraint p_table_id_to_t_id foreign key (table_id) references tables (id)
);

CREATE TABLE player_states
(
    id            uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    player_id     uuid                                        NOT NULL,
    --table_id      INT                NOT NULL,
    in_game_state VARCHAR(15)                                 NOT NULL,
    round_role    VARCHAR(15)                                 NOT NULL,
    constraint ps_player_id_to_p_id foreign key (player_id) references players (id)
);

CREATE TABLE cards
(
    id     uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    symbol VARCHAR(10)                                 NOT NULL,
    rank   VARCHAR(5)                                  NOT NULL
);

DO $$

    DECLARE
        symbol  TEXT[] := ARRAY ['HEARTH', 'DIAMOND', 'CLUB', 'SPADE'];
        ranks   TEXT[] := ARRAY ['ACE', 'KING', 'QUEEN', 'JACK', 'TEN', 'NINE', 'EIGHT', 'SEVEN', 'SIX', 'FIVE', 'FOUR', 'THREE', 'TWO'];
        symbol1 TEXT;
        rank1   TEXT;
    BEGIN
        -- Loop through each suit and rank to insert the cards
        FOREACH symbol1 IN ARRAY symbol
            LOOP
                FOREACH rank1 IN ARRAY ranks
                    LOOP
                        INSERT INTO cards (symbol, rank) VALUES (symbol1, rank1);
                    END LOOP;
            END LOOP;
    END $$;

CREATE TABLE hands
(
    id        uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    player_id uuid                                        NOT NULL,
    constraint h_player_id_to_p_id foreign key (player_id) references players (id)
);

CREATE TABLE card_owners
(
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    card_id   uuid NOT NULL,
    table_id  uuid ,
    hand_id uuid ,

    constraint co_card_id_to_c_id foreign key (card_id) references cards (id),
    constraint co_table_id_to_t_id foreign key (table_id) references tables (id),
    constraint co_hand_id_to_h_id foreign key (hand_id) references hands (id)
);