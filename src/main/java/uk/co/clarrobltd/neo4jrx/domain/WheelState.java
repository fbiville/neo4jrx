package uk.co.clarrobltd.neo4jrx.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.co.clarrobltd.neo4jrx.util.LocalDateTimeSupport;

import java.time.LocalDateTime;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("WheelState")
public class WheelState
{
    @Id
    @GeneratedValue
    private Long internalId;
    private final String externalId;
    private final String name;
    private final String description;
    @JsonSerialize(using = LocalDateTimeSupport.Serializer.class)
    @JsonDeserialize(using = LocalDateTimeSupport.Deserializer.class)
    private LocalDateTime from;
    @JsonSerialize(using = LocalDateTimeSupport.Serializer.class)
    @JsonDeserialize(using = LocalDateTimeSupport.Deserializer.class)
    private LocalDateTime to;
    @Relationship(type = "PREVIOUS_STATE", direction = OUTGOING)
    private WheelState previousState;

    public WheelState(
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

    public Long getInternalId()
    {
        return internalId;
    }

    public void setInternalId(long internalId)
    {
        this.internalId = internalId;
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

    public LocalDateTime getFrom()
    {
        return from;
    }

    public void setFrom(LocalDateTime from)
    {
        this.from = from;
    }

    public LocalDateTime getTo()
    {
        return to;
    }

    public void setTo(LocalDateTime to)
    {
        this.to = to;
    }

    public WheelState getPreviousState()
    {
        return previousState;
    }

    public void setPreviousState(final WheelState previousState)
    {
        this.previousState = previousState;
    }
}
