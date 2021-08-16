package org.ianitrix.kafka.apm.example.billing;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.ianitrix.kafka.apm.example.billing.pojo.Document;
import org.ianitrix.kafka.apm.example.billing.pojo.MultiDocuments;
import org.ianitrix.kafka.apm.example.billing.pojo.SetTraceHeaderTransformer;

import java.time.Duration;

@Slf4j
public class AggregatePaymentAndInvoiceTopology {

    private final Serde<MultiDocuments> multiDocSerde = SerdesUtils.createJsonSerdes(MultiDocuments.class);
    private final Serde<Document> documentSerde = SerdesUtils.createJsonSerdes(Document.class);

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Document> documentStream = builder.stream("document", Consumed.with(Serdes.String(), documentSerde).withName("input"))
                .transformValues(() -> new ExtractTraceHeaderTransformer());
        documentStream.foreach((k,v) -> log.error("################" +  v.toString()));

        final KGroupedStream<String, Document> groupedStreamByClientId = documentStream.groupBy((k, v) -> v.getClientId(), Grouped.with(Serdes.String(), documentSerde).withName("groupBy"));
        final TimeWindowedKStream<String, Document> timeWindowedGroupedStreamByClientId = groupedStreamByClientId.windowedBy(TimeWindows.of(Duration.ofSeconds(30)).grace(Duration.ZERO));

        final KTable<Windowed<String>, MultiDocuments> groupBillPayment = timeWindowedGroupedStreamByClientId.aggregate(
                () -> MultiDocuments.builder().build(),
                this::aggregate,
                Named.as("agg"),
                Materialized.<String,MultiDocuments>as(Stores.persistentWindowStore("agg",Duration.ofSeconds(30), Duration.ofSeconds(30),false)).withKeySerde(Serdes.String()).withValueSerde(multiDocSerde));


        final KStream<String, MultiDocuments> result = groupBillPayment//.suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()).withName("supress"))
                .toStream()
                .map((k, v) -> KeyValue.pair(k.key(), v))
                .transformValues(() -> new SetTraceHeaderTransformer());

        result.foreach((k,v) -> log.error("################" +  v.toString()));
        result.to("fusionAggDoc", Produced.with(Serdes.String(), multiDocSerde).withName("out"));

        return builder.build();
    }

    private MultiDocuments aggregate(final String clientId, final Document newValue, final MultiDocuments oldValue) {
        if ("invoice".equals(newValue.getType())) {
            oldValue.setInvoiceId(newValue.getDocumentId());
        } else {
            oldValue.setPaymentId(newValue.getDocumentId());
        }
        oldValue.setTraceParent(newValue.getTraceParent());
        return oldValue;
    }
}
