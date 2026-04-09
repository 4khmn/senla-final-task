ALTER TABLE advertisements
    ADD COLUMN top_until TIMESTAMP;

CREATE INDEX idx_ads_top_sorting ON advertisements (is_top, top_until, created_at DESC);

ALTER TABLE sales_history
    ADD COLUMN was_top BOOLEAN NOT NULL DEFAULT FALSE