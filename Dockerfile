# 1. Builder stage: install deps & build the app
FROM node:23-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 2. Production stage: serve via nginx
FROM nginx:stable-alpine AS production

# Copy built static files
COPY --from=builder /app/dist/ /usr/share/nginx/html/

EXPOSE 5173
CMD ["nginx", "-g", "daemon off;"]
