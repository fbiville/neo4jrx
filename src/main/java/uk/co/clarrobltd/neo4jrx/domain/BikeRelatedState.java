package uk.co.clarrobltd.neo4jrx.domain;

import java.util.List;

public class BikeRelatedState
{
    private final List<WheelState> wheels;

    public BikeRelatedState(final List<WheelState> wheels)
    {
        this.wheels = wheels;
    }

    public List<WheelState> getWheels()
    {
        return wheels;
    }
}
