ALTER TABLE article_index ADD COLUMN tags TEXT[];

CREATE INDEX article_index_tags ON article_index USING GIN (tags);