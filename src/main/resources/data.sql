INSERT INTO api_key (key_value, enabled)
VALUES ('mi-api-key-valida', TRUE)
ON CONFLICT (key_value) DO NOTHING;

INSERT INTO api_key (key_value, enabled)
VALUES ('mi-api-key-invalida', FALSE)
ON CONFLICT (key_value) DO NOTHING;
