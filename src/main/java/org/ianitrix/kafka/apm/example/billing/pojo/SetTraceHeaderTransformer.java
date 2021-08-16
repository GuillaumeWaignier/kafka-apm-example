package org.ianitrix.kafka.apm.example.billing.pojo;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.nio.charset.StandardCharsets;

public class SetTraceHeaderTransformer implements ValueTransformer<MultiDocuments,MultiDocuments> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
    }

    @Override
    public MultiDocuments transform(MultiDocuments document) {
        this.context.headers().add("toto",document.getTraceParent().getBytes(StandardCharsets.UTF_8));

        this.context.headers().remove("traceparent");
        this.context.headers().add("traceparent",document.getTraceParent().getBytes(StandardCharsets.UTF_8));
        return document;
    }

    @Override
    public void close() {

    }
}
