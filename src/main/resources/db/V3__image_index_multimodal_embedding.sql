ALTER TABLE image_index ADD COLUMN multimodal_embedding vector(1024);
ALTER TABLE image_index ADD COLUMN has_rights boolean;

CREATE INDEX ON image_index USING ivfflat (multimodal_embedding vector_cosine_ops);
CREATE INDEX ON image_index (has_rights);