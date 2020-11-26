package uk.co.clarrobltd.neo4jrx.domain;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.springframework.data.core.schema.Relationship.Direction.OUTGOING;

@Node("Wheel")
public class WheelKey
{
    @Id @GeneratedValue
    private long internalId;
    private final String externalId;
    @Relationship(type = "CURRENT_STATE", direction = OUTGOING)
    private WheelState currentState;
    @Relationship(type = "WHEEL_STATE", direction = OUTGOING)
    private List<WheelState> wheelStates = new ArrayList<>();

    public WheelKey(final String externalId, final WheelState currentState, final List<WheelState> wheelStates)
    {
        this.externalId = externalId;
        this.currentState = currentState;
        this.wheelStates = wheelStates;
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

    public WheelState getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(WheelState currentState)
    {
        this.currentState = currentState;
    }

    public List<WheelState> getWheelStates()
    {
        return wheelStates;
    }

    public void addWheelState(final WheelState wheelState)
    {
        wheelStates.add(wheelState);
    }
}
