ALTER TABLE image_index ADD COLUMN ia_generated boolean;

CREATE INDEX ON image_index (ia_generated);