package uk.co.clarrobltd.neo4jrx.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSupport
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnn");

    private LocalDateTimeSupport()
    {
        // static class never constructed
    }

    public static class Serializer extends StdSerializer<LocalDateTime>
    {
        private Serializer()
        {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(final LocalDateTime localDateTime, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeString(formatter.format(localDateTime));
        }
    }

    public static class Deserializer extends StdDeserializer<LocalDateTime>
    {
        private Deserializer()
        {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException
        {
            return LocalDateTime.parse(jsonParser.getValueAsString());
        }
    }
}
