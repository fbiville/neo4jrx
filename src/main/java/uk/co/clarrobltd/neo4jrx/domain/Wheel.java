package uk.co.clarrobltd.neo4jrx.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class Wheel
{
    private final String externalId;
    private final String name;
    private final String description;
    private final LocalDateTime from;
    private final LocalDateTime to;

    public Wheel(
            final String externalId,
            final String name,
            final String description,
            final LocalDateTime from,
            final LocalDateTime to)
    {
        this.externalId = externalId;
        this.name = name;
        this.description = description;
        this.from = from;
        this.to = to;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public boolean equals(final Object thatObject)
    {
        if (this == thatObject)
        {
            return true;
        }

        if (thatObject == null || getClass() != thatObject.getClass())
        {
            return false;
        }

        final Wheel that = (Wheel) thatObject;

        return new EqualsBuilder()
                .append(externalId, that.externalId)
                .append(name, that.name)
                .append(description, that.description)
                .append(from, that.from)
                .append(to, that.to)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(externalId)
                .append(name)
                .append(description)
                .append(from)
                .append(to)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("externalId", externalId)
                .append("name", name)
                .append("description", description)
                .append("from", from)
                .append("to", to)
                .toString();
    }

    public static void main(final String... arguments) throws JsonProcessingException
    {
        String s = new ObjectMapper().writeValueAsString(new Wheel("1", "Tern", "Folding", LocalDateTime.now(), LocalDateTime.parse("3000-01-01T00:00")));
        System.out.println(s);
    }
}
