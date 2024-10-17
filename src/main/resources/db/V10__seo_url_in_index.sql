ALTER TABLE image_index ADD COLUMN seo_url varchar(255);
CREATE INDEX ON image_index (seo_url);
ALTER TABLE image_content ADD COLUMN seo_url varchar(255);
CREATE INDEX ON image_content (seo_url);

ALTER TABLE article_index ADD COLUMN seo_url varchar(255);
CREATE INDEX ON article_index (seo_url);
ALTER TABLE article_content ADD COLUMN seo_url varchar(255);
CREATE INDEX ON article_content (seo_url);