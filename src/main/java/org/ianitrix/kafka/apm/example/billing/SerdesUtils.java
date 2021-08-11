package org.ianitrix.kafka.apm.example.billing;

import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to create Json serdes.
 */
public final class SerdesUtils
{
    private SerdesUtils() {
    }

    /**
     * Create Serdes from Json class
     * @param clazz Json class
     * @param <T> Type of the json class
     * @return json Serde
     */
    public static <T> Serde<T> createJsonSerdes(final Class<T> clazz) {
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("json.value.type", clazz);
        final Serializer<T> serializer = new KafkaJsonSerializer<>();
        serializer.configure(serdeProps, false);
        final Deserializer<T> deserializer = new KafkaJsonDeserializer<>();
        deserializer.configure(serdeProps, false);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}