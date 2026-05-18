#!/bin/sh
set -eu

api_base_url="${API_BASE_URL:-http://localhost:8080}"
google_client_id="${GOOGLE_CLIENT_ID:-}"

cat > /usr/share/nginx/html/config.js <<EOF
window.__MINITASKFLOW_CONFIG__ = { apiBaseUrl: "${api_base_url}", googleClientId: "${google_client_id}" };
EOF
