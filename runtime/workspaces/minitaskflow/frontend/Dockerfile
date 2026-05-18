FROM nginx:1.27-alpine

COPY dist /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY nginx/docker-entrypoint.sh /docker-entrypoint.d/40-minitaskflow-config.sh

RUN chmod +x /docker-entrypoint.d/40-minitaskflow-config.sh

EXPOSE 80
