#!/bin/bash

# ===============================================
# 🦩 PÉLICAN - Script de Sauvegarde PostgreSQL
# ===============================================

# Configuration
DB_NAME="pelican_db"
DB_USER="pelican_user"
DB_PASSWORD="changeme_strong_password"
DB_HOST="localhost"
BACKUP_DIR="$HOME/IdeaProjects/pelican-backend/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/pelican_backup_$DATE.sql"
RETENTION_DAYS=7

# Utiliser pg_dump 15
PG_DUMP="/opt/homebrew/opt/postgresql@15/bin/pg_dump"

# Si pg_dump 15 n'existe pas, utiliser le pg_dump par défaut
if [ ! -f "$PG_DUMP" ]; then
    PG_DUMP="pg_dump"
fi

# Créer le dossier de backup
mkdir -p "$BACKUP_DIR"

echo "================================================"
echo "🦩 SAUVEGARDE PÉLICAN - $(date)"
echo "================================================"
echo "📌 pg_dump utilisé: $PG_DUMP"

# Faire le backup
echo "📦 Création de la sauvegarde..."
PGPASSWORD=$DB_PASSWORD $PG_DUMP -U $DB_USER -h $DB_HOST -d $DB_NAME > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    # Compresser le backup
    gzip "$BACKUP_FILE"
    echo "✅ Sauvegarde réussie: ${BACKUP_FILE}.gz"
    
    # Taille du fichier
    SIZE=$(ls -lh "${BACKUP_FILE}.gz" | awk '{print $5}')
    echo "📊 Taille: $SIZE"
    
    # Supprimer les anciens backups
    echo "🗑️  Suppression des anciens backups (> $RETENTION_DAYS jours)..."
    find "$BACKUP_DIR" -name "pelican_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    
    BACKUP_COUNT=$(ls -1 "$BACKUP_DIR"/pelican_backup_*.sql.gz 2>/dev/null | wc -l)
    echo "📁 Total des backups: $BACKUP_COUNT"
    
    echo "================================================"
    echo "✅ SAUVEGARDE TERMINÉE AVEC SUCCÈS"
    echo "================================================"
else
    echo "❌ ERREUR lors de la sauvegarde!"
    rm -f "$BACKUP_FILE"
    exit 1
fi
