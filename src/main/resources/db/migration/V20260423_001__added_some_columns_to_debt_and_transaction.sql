ALTER TABLE debts
    ADD paid_amount DOUBLE PRECISION;

ALTER TABLE debts
    ADD remaining_amount DOUBLE PRECISION;

ALTER TABLE debts
    ALTER COLUMN paid_amount SET NOT NULL;

ALTER TABLE debts
    ALTER COLUMN remaining_amount SET NOT NULL;

ALTER TABLE debts
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE transactions
    ALTER COLUMN amount SET NOT NULL;