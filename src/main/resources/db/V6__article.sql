CREATE TABLE IF NOT EXISTS article_index
(
    id                              bigint PRIMARY KEY,
    keywords                        TEXT[],
    site_categories                 TEXT[],
    product_categories              TEXT[],
    status                          varchar(50) NOT NULL,
    create_instant                  timestamptz NOT NULL,
    update_instant                  timestamptz NOT NULL,
    publish_instant                 timestamptz
);

CREATE INDEX article_index_keywords ON article_index USING GIN (keywords);
CREATE INDEX article_index_site_categories ON article_index USING GIN (site_categories);
CREATE INDEX article_index_product_categories ON article_index USING GIN (product_categories);
CREATE INDEX article_index_status ON article_index (status);
CREATE INDEX article_index_create_instant ON article_index (create_instant);
CREATE INDEX article_index_update_instant ON article_index (update_instant);
CREATE INDEX article_index_publish_instant ON article_index (publish_instant);

CREATE TABLE IF NOT EXISTS article_content
(
    id                              bigint PRIMARY KEY REFERENCES article_index (id),
    content                         jsonb
);

CREATE TABLE IF NOT EXISTS article_image(
    image_id                        bigint REFERENCES image_index (id),
    article_id                      bigint REFERENCES article_index (id)
);