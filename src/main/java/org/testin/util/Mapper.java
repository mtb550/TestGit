package org.testin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.TimeZone;

public class Mapper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setTimeZone(TimeZone.getDefault());

    public static <T> T readValue(final @NotNull File src, final @NotNull Class<T> valueType) {
        try {
            return mapper.readValue(src, valueType);

        } catch (Exception e) {
            System.err.println("Failed to read file path " + src + ". to class " + valueType.getSimpleName());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static byte[] writeValueAsBytes(final Object value) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(value);
        } catch (Exception e) {
            System.err.println("Failed to serialize object to bytes: " + value.getClass().getSimpleName());
            e.printStackTrace(System.err);
            return new byte[0];
        }
    }
}
