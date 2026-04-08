ALTER TABLE advertisements
    ADD COLUMN top_until TIMESTAMP;

CREATE INDEX idx_ads_top_sorting ON advertisements (is_top, top_until, created_at DESC);