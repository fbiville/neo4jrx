package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Objects;

@Node("BikeState")
public class BikeState {
    @Id
    @GeneratedValue
    private Long internalId;
    private final String externalId;
    private final String name;
    private final String description;

    public BikeState(
            final String externalId,
            final String name,
            final String description) {
        this.externalId = externalId;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BikeState bikeState = (BikeState) o;
        return Objects.equals(internalId, bikeState.internalId) && Objects.equals(externalId, bikeState.externalId) && Objects.equals(name, bikeState.name) && Objects.equals(description, bikeState.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, externalId, name, description);
    }

    @Override
    public String toString() {
        return "BikeState{" +
                "internalId=" + internalId +
                ", externalId='" + externalId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
