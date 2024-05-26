Create Table balances
(
    id   SERIAL PRIMARY KEY,
    cash NUMERIC(10, 2),
    bet  NUMERIC(10, 2)
);

Create Table players
(
    id         SERIAL PRIMARY KEY NOT NULL,
    name       VARCHAR(25) NOT NULL,
    balance_id INTEGER NOT NULL,
    state      VARCHAR(15) NOT NULL,
    constraint p_balance_id_to_b_id foreign key (balance_id) references balances(id)
);

select *
from balances;
