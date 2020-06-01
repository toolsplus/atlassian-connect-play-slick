# --- !Ups
ALTER TABLE atlassian_host ADD COLUMN display_url VARCHAR;
ALTER TABLE atlassian_host ADD COLUMN display_url_servicedesk_help_center VARCHAR;

# --- !Downs
ALTER TABLE atlassian_host DROP COLUMN display_url VARCHAR;
ALTER TABLE atlassian_host DROP COLUMN display_url_servicedesk_help_center VARCHAR;
