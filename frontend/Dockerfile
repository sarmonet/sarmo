FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json package-lock.json ./

RUN npm install --frozen-lockfile

COPY . .

RUN npm run build

FROM node:20-alpine

WORKDIR /app

COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/next.config.ts ./
COPY --from=builder /app/next-i18next.config.js ./

RUN npm install --production --frozen-lockfile

COPY .env.local .env.local

EXPOSE 3000

CMD ["npm", "start"]
