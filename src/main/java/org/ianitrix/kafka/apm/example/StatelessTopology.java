package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

@Slf4j
public class StatelessTopology {

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> input = builder.stream("test3");
        final KStream<String,String> json = input.mapValues(s -> "{\"id\":\"" + s + "\"}");
        json.to("test_json_kstream_stateless");

        return builder.build();
    }
}
