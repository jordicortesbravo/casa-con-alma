CREATE TABLE IF NOT EXISTS scraped_document_image(
    image_id                        bigint REFERENCES image_index (id),
    scraped_document_id             bigint REFERENCES scraped_document_index (id)
);

