package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Objects;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node("Bike")
public class BikeKey {
    @Id
    @GeneratedValue
    private Long internalId;
    private final String externalId;
    @Relationship(type = "CURRENT_STATE", direction = OUTGOING)
    private BikeState currentState;

    public BikeKey(final String externalId, final BikeState currentState) {
        this.externalId = externalId;
        this.currentState = currentState;
    }

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public BikeState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(BikeState currentState) {
        this.currentState = currentState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BikeKey bikeKey = (BikeKey) o;
        return Objects.equals(internalId, bikeKey.internalId) && Objects.equals(externalId, bikeKey.externalId) && Objects.equals(currentState, bikeKey.currentState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, externalId, currentState);
    }

    @Override
    public String toString() {
        return "BikeKey{" +
                "internalId=" + internalId +
                ", externalId='" + externalId + '\'' +
                ", currentState=" + currentState +
                '}';
    }
}