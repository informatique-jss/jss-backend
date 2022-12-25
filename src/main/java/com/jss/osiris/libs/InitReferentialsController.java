package com.jss.osiris.libs;

// ALTER TABLE index_entity ADD COLUMN ts_text tsvector GENERATED ALWAYS AS (to_tsvector('french', text)) STORED;
// CREATE INDEX idx_ts_text ON index_entity USING GIST (ts_text);  
//CREATE INDEX idx_text ON index_entity USING GIST (text gist_trgm_ops );  
// CREATE EXTENSION pg_trgm;
public class InitReferentialsController {
}
