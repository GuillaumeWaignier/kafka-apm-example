FROM openjdk:11.0.3-slim

ENV PATH ${PATH}:/kafka-apm-example/bin

COPY target/kafka-apm-example-*-bin.tar.gz /

RUN  echo "install kafka-apm-example" \
  && tar xzf /kafka-apm-example-*-bin.tar.gz \
  && rm /kafka-apm-example-*-bin.tar.gz \
  && ln -s /kafka-apm-example-* /kafka-apm-example

WORKDIR /kafka-apm-example

EXPOSE 8080

ENTRYPOINT ["kafka-apm-example.sh"]