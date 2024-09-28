ALTER TABLE scraped_document_index ADD COLUMN embedding vector(1024);

CREATE INDEX ON scraped_document_index USING ivfflat (embedding vector_cosine_ops);