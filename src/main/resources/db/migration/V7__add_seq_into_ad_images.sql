CREATE SEQUENCE IF NOT EXISTS ad_images_seq
    START WITH 1
    INCREMENT BY 20;

SELECT setval('ad_images_seq', (SELECT COALESCE(MAX(id), 1) FROM ad_images));