package org.ianitrix.kafka.apm.example.billing;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.ianitrix.kafka.apm.example.billing.pojo.Document;

import java.nio.charset.StandardCharsets;

public class ExtractTraceHeaderTransformer implements ValueTransformer<Document,Document> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
    }

    @Override
    public Document transform(Document document) {

        final Header traceParent = this.context.headers().lastHeader("traceparent");
        document.setTraceParent(new String(traceParent.value(), StandardCharsets.UTF_8));
        return document;
    }

    @Override
    public void close() {

    }
}
