ALTER TABLE article_index ADD COLUMN embedding vector(1024);

CREATE INDEX ON article_index USING ivfflat (embedding vector_cosine_ops);