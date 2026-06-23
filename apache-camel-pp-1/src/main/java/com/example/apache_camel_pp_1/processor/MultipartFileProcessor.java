package com.example.apache_camel_pp_1.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

@Component
public class MultipartFileProcessor implements Processor {

    private static final String BOUNDARY = "----CamelBoundary";

    @Override
    public void process(Exchange exchange) throws Exception {

        File file = exchange.getMessage().getBody(File.class);
        String fileName = exchange.getMessage()
                .getHeader(Exchange.FILE_NAME, String.class);

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(("--" + BOUNDARY + "\r\n").getBytes());
        out.write(("Content-Disposition: form-data; name=\"file\"; filename=\""
                + fileName + "\"\r\n").getBytes());
        out.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());
        out.write(fileBytes);
        out.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());

        exchange.getMessage().setBody(out.toByteArray());
        exchange.getMessage().setHeader(Exchange.HTTP_METHOD, "POST");
        exchange.getMessage().setHeader(
                Exchange.CONTENT_TYPE,
                "multipart/form-data; boundary=" + BOUNDARY
        );
    }
}