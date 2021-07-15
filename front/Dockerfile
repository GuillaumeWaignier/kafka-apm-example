#
# Build stage
#
FROM node:16-alpine3.12 AS build

COPY . /build

RUN cd /build && \
    npm install

WORKDIR /build
EXPOSE 4200
ENTRYPOINT ["npm","run","start"]
