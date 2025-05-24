FROM node:23-alpine

WORKDIR /app

# 1) Install deps ahead of time
COPY package*.json ./
RUN npm ci

# 2) Copy your code
COPY . .

# 3) Expose the Vite port
EXPOSE 5173

# 4) Start Vite in the foreground
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
