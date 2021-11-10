#!/bin/bash

# Load database connection info
source .env 

# Read query into a variable
sql="$(cat setup.sql)"

# If psql is not installed, then exit
if ! command -v psql > /dev/null; then 
  echo "PostgreSQL is required..."
  exit 1 
fi 

# Connect to the database, run the query, then disconnect
PGPASSWORD="$POSTGRES_PASSWORD" psql -t -A \
-h "$DBHOST" \
-p "$DBHOSTPORT" \
-d "$DBNAME" \
-U "$DBUSERNAME" \
-c "$sql" 