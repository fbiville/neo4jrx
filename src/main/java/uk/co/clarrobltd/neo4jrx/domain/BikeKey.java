package uk.co.clarrobltd.neo4jrx.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;
import static org.neo4j.springframework.data.core.schema.Relationship.Direction.OUTGOING;

@Node("Bike")
public class BikeKey
{
    @Id @GeneratedValue
    private long internalId;
    private final String externalId;
    @Relationship(type = "CURRENT_STATE", direction = OUTGOING)
    private BikeState currentState;
    @Relationship(type = "BIKE_STATE", direction = OUTGOING)
    private List<BikeState> bikeStates = new ArrayList<>();

    public BikeKey(final String externalId, final BikeState currentState, final List<BikeState> bikeStates)
    {
        this.externalId = externalId;
        this.currentState = currentState;
        this.bikeStates = bikeStates;
    }

    public long getInternalId()
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

    public BikeState getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(BikeState currentState)
    {
        this.currentState = currentState;
    }

    public List<BikeState> getBikeStates()
    {
        return bikeStates;
    }

    public void addBikeState(final BikeState bikeState)
    {
        bikeStates.add(bikeState);
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

        final BikeKey that = (BikeKey) thatObject;

        return new EqualsBuilder()
                .append(internalId, that.internalId)
                .append(externalId, that.externalId)
                .append(currentState, that.currentState)
                .append(bikeStates, that.bikeStates)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(internalId)
                .append(externalId)
                .append(currentState)
                .append(bikeStates)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, JSON_STYLE)
                .append("internalId", internalId)
                .append("externalId", externalId)
                .append("currentState", currentState)
                .append("bikeStates", bikeStates)
                .toString();
    }
}
