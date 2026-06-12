FROM node:22-alpine AS frontend
WORKDIR /app/frontend
ARG VITE_TURNSTILE_SITE_KEY
ENV VITE_TURNSTILE_SITE_KEY=${VITE_TURNSTILE_SITE_KEY}
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM caddy:2
COPY --from=frontend /app/frontend/dist /srv
COPY Caddyfile /etc/caddy/Caddyfile
COPY caddy/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
