# Stage 1: Build
FROM node:22-alpine AS builder

# Enable corepack for pnpm
RUN corepack enable

WORKDIR /app

# Copy lockfile and manifest
COPY package.json pnpm-lock.yaml ./

# Install dependencies (frozen lockfile for reproducibility)
RUN pnpm install --frozen-lockfile

# Copy source code
COPY . .

# Build arguments with defaults
ARG VITE_MOCK_DATA=true
ARG VITE_API_BASE_URL=http://localhost:8080

# Pass build args as environment variables to the build process
ENV VITE_MOCK_DATA=${VITE_MOCK_DATA}
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# Build the application
RUN pnpm build

# Stage 2: Runtime
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built application from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/index.html || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
