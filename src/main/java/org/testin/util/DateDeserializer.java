package org.testin.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.testin.pojo.Config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class DateDeserializer extends JsonDeserializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.trim().isEmpty()) {
            return ZonedDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        }

        try {
            return ZonedDateTime.parse(text, DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy 'At' HH:mm:ss '['VV']'", Locale.US));
        } catch (Exception ignored) {
        }

        try {
            DateTimeFormatter legacyFormatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("EEE hh:mm a dd.MM.yyyy")
                    .toFormatter(Locale.US);
            LocalDateTime localDateTime = LocalDateTime.parse(text, legacyFormatter);
            return localDateTime.atZone(ZoneId.systemDefault());
        } catch (Exception ignored) {
        }

        try {
            DateTimeFormatter configFormatter = DateTimeFormatter.ofPattern(Config.DATE_FORMAT_PATTERN, Locale.US);
            LocalDateTime localDateTime = LocalDateTime.parse(text, configFormatter);
            return localDateTime.atZone(ZoneId.systemDefault());
        } catch (Exception ignored) {
        }

        return ZonedDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    }
}