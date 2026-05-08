-- Créer la base de données
CREATE DATABASE pelican_db;

-- Créer l'utilisateur et lui donner les droits
CREATE USER pelican_user WITH ENCRYPTED PASSWORD 'changeme_strong_password';

-- Donner tous les droits sur la base
\c pelican_db
GRANT ALL PRIVILEGES ON DATABASE pelican_db TO pelican_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO pelican_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO pelican_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO pelican_user;

-- Activer les extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
