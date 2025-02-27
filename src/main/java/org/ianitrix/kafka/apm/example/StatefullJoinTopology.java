package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

@Slf4j
public class StatefullJoinTopology {

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<byte[], String> input1 = builder.stream("test2");
        final KTable<String, String> input2 = builder.stream("test1", Consumed.with(Serdes.String(), Serdes.String()))
                .selectKey((k, v) -> v)
                .toTable(Materialized.<String, String>as(Stores.persistentKeyValueStore("table_test_1_join")).withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));

        final KStream<String, String> joined = input1.selectKey((k, v) -> v)
                .mapValues( s -> {
                    if ("error".equals(s)) {
                        final NullPointerException e = new NullPointerException();
                        log.error("Error produce with 'error' message received in topic2", e);
                        throw e;
                    }
                    log.info("Handle message " + s);
                    return s;
                })
                .leftJoin(input2, (v1, v2) -> "{\"id\":\"" + v1 + "\"}", Joined.with(Serdes.String(), Serdes.String(), Serdes.String()))
                .peek( (k,v) -> log.info("Joined message " + v));
        joined.to("test_json_kstream_statefullJoin", Produced.with(Serdes.String(),Serdes.String()));

        return builder.build();
    }
}
