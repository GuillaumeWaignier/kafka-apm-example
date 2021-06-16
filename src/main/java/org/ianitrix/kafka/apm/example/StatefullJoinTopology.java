package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

@Slf4j
public class StatefullJoinTopology {

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<byte[], String> input1 = builder.stream("test2");
        final KTable<String, String> input2 = builder.table("test1", Consumed.with(Serdes.String(), Serdes.String()));

        final KStream<String, String> joined = input1.selectKey((k, v) -> v)
                .leftJoin(input2, (v1, v2) -> "{\"id\":\"" + v1 + "\"}", Joined.with(Serdes.String(), Serdes.String(), Serdes.String()));
        joined.to("test_json_kstream_statefullJoin", Produced.with(Serdes.String(),Serdes.String()));

        return builder.build();
    }
}
