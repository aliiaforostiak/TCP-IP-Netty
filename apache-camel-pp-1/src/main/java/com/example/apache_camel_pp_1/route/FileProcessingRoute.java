package com.example.apache_camel_pp_1.route;

import com.example.apache_camel_pp_1.processor.MultipartFileProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileProcessingRoute extends RouteBuilder {

    private final MultipartFileProcessor multipartFileProcessor;

    public FileProcessingRoute(MultipartFileProcessor multipartFileProcessor) {
        this.multipartFileProcessor = multipartFileProcessor;
    }

    @Override
    public void configure() {

        onException(Exception.class)
                .log("FAILED file=${header.CamelFileName}, reason=${exception.message}")
                .handled(true);

        from("file:{{app.file.input-dir}}"
                + "?delete=true"
                + "&readLock=changed"
                + "&delay=3000"
                + "&moveFailed=.failed/${file:name}")

                .routeId("file-processing-route")

                .log("Picked file: ${header.CamelFileName}, size=${header.CamelFileLength}")

                .choice()

                .when(simple("${header.CamelFileName} endsWith '.txt' "
                        + "&& ${header.CamelFileLength} <= {{app.file.max-text-size}}"))

                .convertBodyTo(String.class)
                .log("TEXT → Kafka: ${header.CamelFileName}")
                .to("kafka:{{app.kafka.topic}}")

                .when(simple("${header.CamelFileLength} > {{app.file.max-text-size}}"))

                .log("BIG FILE → API: ${header.CamelFileName}")
                .process(multipartFileProcessor)
                .toD("{{app.api.upload-url}}")

                .otherwise()

                .log("UNSUPPORTED FILE TYPE: ${header.CamelFileName}");
    }
}
