package uk.co.clarrobltd.neo4jrx.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.co.clarrobltd.neo4jrx.util.LocalDateTimeSupport;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("BikeState")
public class BikeState
{
    @Id @GeneratedValue
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
    private BikeState previousState;
    @Relationship(type = "HAS_COMPONENT", direction = OUTGOING)
    private List<WheelState> wheels;

    public BikeState(
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

    public BikeState getPreviousState()
    {
        return previousState;
    }

    public void setPreviousState(final BikeState previousState)
    {
        this.previousState = previousState;
    }

    public List<WheelState> getWheels()
    {
        return wheels;
    }

    public void setWheels(final List<WheelState> wheels)
    {
        this.wheels = wheels;
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

        final BikeState that = (BikeState) thatObject;

        return new EqualsBuilder()
                .append(internalId, that.internalId)
                .append(externalId, that.externalId)
                .append(name, that.name)
                .append(description, that.description)
                .append(from, that.from)
                .append(to, that.to)
                .append(previousState, that.previousState)
                .append(wheels, that.wheels)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(internalId)
                .append(externalId)
                .append(name)
                .append(description)
                .append(from)
                .append(to)
                .append(previousState)
                .append(wheels)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("internalId", internalId)
                .append("externalId", externalId)
                .append("name", name)
                .append("description", description)
                .append("from", from)
                .append("to", to)
                .append("previousState", previousState)
                .append("wheels", wheels)
                .toString();
    }
}
