ALTER TABLE image_index 
ADD COLUMN status varchar(50),
ADD COLUMN publish_instant timestamptz;

CREATE INDEX image_index_status ON image_index (status);
CREATE INDEX image_index_publish_instant ON image_index (publish_instant);