package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;

@Slf4j
public class StatefullTransformTopology {

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<byte[], String> input1 = builder.stream("test4");

        final KStream<String, String> res = input1.transform(() -> new FooTransformer());
        res.to("test_json_kstream_statefullTransform", Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }

    public class FooTransformer implements Transformer<byte[], String, KeyValue<String,String>> {

        @Override
        public void init(ProcessorContext processorContext) {
        }

        @Override
        public KeyValue<String,String> transform(byte[] key, String value) {
            return KeyValue.pair(value, "{\"name\":\"" + value + "\"}");
        }

        @Override
        public void close() {

        }
    }
}
