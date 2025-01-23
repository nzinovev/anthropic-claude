-- Create role with login capability
CREATE ROLE anthropic_claude_app WITH
    LOGIN
    PASSWORD 'strongPassword'
    NOSUPERUSER
    INHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION;

-- Create schema
CREATE SCHEMA anthropic_claude AUTHORIZATION anthropic_claude_app;

-- Grant usage on schema to app role
GRANT USAGE ON SCHEMA anthropic_claude TO anthropic_claude_app;

-- Grant all privileges on all tables in schema to app role
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA anthropic_claude TO anthropic_claude_app;

-- Set search path for the app role
ALTER ROLE anthropic_claude_app SET search_path TO anthropic_claude;