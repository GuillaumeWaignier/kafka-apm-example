package org.ianitrix.kafka.apm.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

/**
 * Main class
 *
 * @author Guillaume Waignier
 *
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting ...");
        final Instant startTime = Instant.now();

        checkArgument(args);

        final Properties config = computeStreamConfig(args[0]);

        final String mode = config.getProperty("mode");
        Topology topology = null;
        if ("map".equals(mode)) {
            topology = new StatelessTopology().buildStream();
        } else if ("join".equals(mode)) {
            topology = new StatefullJoinTopology().buildStream();
        } else {
            exitWithError("Unknown mode " + mode + " / Possible mode are 'map' or 'join'", new IllegalArgumentException());
        }

        log.info(topology.describe().toString());

        final KafkaStreams streams = new KafkaStreams(topology, config);
        setupShutdownHook(streams);
        streams.start();

        final Instant endTime = Instant.now();
        log.info("Started in {} ms", Duration.between(startTime, endTime).toMillis());
    }

    public static Properties computeStreamConfig(final String file) {
        final Properties config = getDefaultStreamConfig();
        config.putAll(loadConfigurationFile(file));
        return config;
    }

    private static Properties getDefaultStreamConfig() {
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-apm-stateless");
        settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArraySerde.class);
        settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        settings.put(StreamsConfig.TOPOLOGY_OPTIMIZATION, StreamsConfig.NO_OPTIMIZATION);
        settings.put(
                StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndContinueExceptionHandler.class.getName());
        return settings;
    }

    private static void setupShutdownHook(KafkaStreams streams) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stop stream");
            streams.close();
        }));
    }

    private static void checkArgument(final String[] args) {
        if (args.length != 1) {
            exitWithError("Missing argument for configuration file\nCommand line are : java - jar AggregatorTraceStream.jar config.properties", null);
        }
    }

    public static Properties loadConfigurationFile(final String file) {
        final File propertiesFile = new File(file);
        final Properties properties = new Properties();

        try (final FileInputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (final IOException e) {
            exitWithError("Error when loading configuration file " + file, e);
        }

        return properties;
    }

    private static void exitWithError(final String errorMessage, final Exception exception) {
        log.error(errorMessage, exception);
        System.exit(1);
    }
}