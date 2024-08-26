CREATE TABLE IF NOT EXISTS scraped_document_index
(
    id                              bigint PRIMARY KEY,
    source_id                       varchar(50) NOT NULL,
    keywords                        TEXT[],
    site_categories                 TEXT[] NOT NULL,
    product_categories              TEXT[],
    create_instant                  timestamptz NOT NULL,
    update_instant                  timestamptz NOT NULL
);

ALTER TABLE scraped_document_index ADD CONSTRAINT scraped_document_index_unique_source_id UNIQUE (source_id);
CREATE UNIQUE INDEX scraped_document_index_source_id ON scraped_document_index (source_id);
CREATE INDEX scraped_document_index_keywords ON scraped_document_index USING GIN (keywords);
CREATE INDEX scraped_document_index_site_categories ON scraped_document_index USING GIN (site_categories);
CREATE INDEX scraped_document_index_product_categories ON scraped_document_index USING GIN (product_categories);
CREATE INDEX scraped_document_index_create_instant ON scraped_document_index (create_instant);
CREATE INDEX scraped_document_index_update_instant ON scraped_document_index (update_instant);

CREATE TABLE IF NOT EXISTS scraped_document_content
(
    id                              bigint PRIMARY KEY REFERENCES scraped_document_index (id),
    source_id                       varchar(50) NOT NULL,
    content                         jsonb
);
CREATE INDEX scraped_document_content_source_id ON scraped_document_content (source_id);
