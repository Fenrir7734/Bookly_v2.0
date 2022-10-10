FROM node:14.19-alpine3.14
WORKDIR /frontend
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 8080
CMD ["npm", "run", "serve"]