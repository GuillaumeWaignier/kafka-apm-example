package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.Stores;

@Slf4j
public class StatefullAggTopology {

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<byte[], String> input1 = builder.stream("test4");

        final KTable<String, String> aggregate = input1.groupBy((k, v) -> v, Grouped.with(Serdes.String(),Serdes.String()).withName("selectKey"))
                .aggregate(() -> null,
                        (key, newValue, oldValue) -> "{\"name\":\"" + newValue + "\"}",
                        Named.as("agg"),
                        Materialized.<String, String>as(Stores.persistentKeyValueStore("agg")));

        final KStream<String, String> res = aggregate.toStream();
        res.to("test_json_kstream_statefullAggregation", Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }
}
