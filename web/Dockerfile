# ------------------
# Build Stage
# ------------------

# build using Node and pnpm.
FROM node:22-alpine AS build
WORKDIR /app

# Install dependencies
COPY package.json pnpm-lock.yaml ./

# Enable pnpm
RUN corepack enable

# Install dependencies
RUN pnpm install --frozen-lockfile --ignore-scripts --store-dir /pnpm/store

# Copy frontend source files
COPY src ./src
COPY public ./public
COPY index.html ./
COPY vite.config.mts ./
COPY tsconfig*.json ./

# Build the frontend and generate static files in /app/dist.
RUN pnpm build-only

# ------------------
# Runtime Stage
# ------------------

# Runtime stage using Nginx.
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime

# Use Nginx configuration to serve the built frontend files and proxy API requests to the backend.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built frontend files from the build stage to the Nginx HTML directory.
COPY --from=build /app/dist /usr/share/nginx/html

# Run container as a non-root user for security.
USER 101:101

# Expose the port that Nginx will listen on.
EXPOSE 8081

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]