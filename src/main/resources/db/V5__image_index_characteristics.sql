ALTER TABLE image_index ADD COLUMN light_intensity real;
ALTER TABLE image_index ADD COLUMN elegance real;

CREATE INDEX ON image_index (light_intensity);
CREATE INDEX ON image_index (elegance);