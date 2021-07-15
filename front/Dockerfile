#
# Build stage
#
FROM node:16-alpine3.12 AS build

COPY . /build

RUN cd /build && \
    npm install && \
    npm run build

WORKDIR /build/dist/front
EXPOSE 4200
ENTRYPOINT ["npx","http-server","-p","4200"]
