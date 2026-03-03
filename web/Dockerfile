# build using Node and pnpm.
FROM node:22-alpine AS build
WORKDIR /app

# Install dependencies
COPY package.json pnpm-lock.yaml ./
RUN corepack enable && pnpm install --frozen-lockfile --ignore-scripts

# Copy files needed for building
COPY src ./src
COPY public ./public
COPY package.json pnpm-lock.yaml ./
COPY index.html ./
COPY vite.config.mts tsconfig*.json ./

# Generate static files in /app/dist.
RUN pnpm build-only

# Stage 2: serve compiled files with non-root Nginx.
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime
# Replace default site config so SPA routes resolve correctly.
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

# Switch to a non root user for security.
USER 101:101

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
