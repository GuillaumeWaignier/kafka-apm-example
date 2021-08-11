package org.ianitrix.kafka.apm.example.billing;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.ianitrix.kafka.apm.example.billing.pojo.Document;
import org.ianitrix.kafka.apm.example.billing.pojo.MultiDocuments;

import java.time.Duration;

@Slf4j
public class JoinPaymentAndInvoiceTopology {

    private final Serde<MultiDocuments> multiDocSerde = SerdesUtils.createJsonSerdes(MultiDocuments.class);
    private final Serde<Document> documentSerde = SerdesUtils.createJsonSerdes(Document.class);

    public Topology buildStream() {

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Document> documentStream = builder.stream("document", Consumed.with(Serdes.String(), documentSerde).withName("input"));

        final KStream<String, Document> input = documentStream.selectKey((k, v) -> v.getClientId(), Named.as("clientIdAsKey"));

        input.foreach((k,v) -> log.error("################" +  v.toString()));

        final KStream<String, Document> invoiceStream = input.filter((k,v) -> "invoice".equals(v.getType()), Named.as("invoice"));
        final KStream<String, Document> paymentStream = input.filter((k,v) -> "payment".equals(v.getType()),Named.as("payment"));

        final KStream<String, MultiDocuments> join = invoiceStream.join(paymentStream,
                this::fusion,
                JoinWindows.of(Duration.ofSeconds(30)),
                StreamJoined.with(Serdes.String(), documentSerde, documentSerde).withName("join"));

        join.foreach((k,v) -> log.error("################" +  v.toString()));

        join.to("fusionJoinDoc", Produced.with(Serdes.String(), multiDocSerde).withName("out"));


        return builder.build();
    }

    private MultiDocuments fusion(final Document invoice, final Document payment) {
        return MultiDocuments.builder().invoiceId(invoice.getDocumentId()).paymentId(payment.getDocumentId()).build();
    }
}
