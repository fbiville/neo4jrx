package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;

@Component
public class WheelTransformer
{
    public WheelKey convertToCreate(final Wheel wheel)
    {
        final WheelState wheelState = new WheelState(
                wheel.getExternalId(),
                wheel.getName(),
                wheel.getDescription(),
                LocalDateTime.now(),
                LocalDateTime.parse("3000-01-01T00:00"));
        return new WheelKey(
                wheel.getExternalId(),
                wheelState,
                singletonList(wheelState));
    }

    public WheelKey convertToUpdate(final Wheel newWheel, final WheelKey currentWheelKey)
    {
        final WheelState currentWheelState = currentWheelKey.getCurrentState();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final WheelState newWheelState = new WheelState(
                newWheel.getExternalId(),
                newWheel.getName(),
                newWheel.getDescription(),
                localDateTime,
                LocalDateTime.parse("3000-01-01T00:00"));
        currentWheelState.setTo(localDateTime);
        currentWheelKey.setCurrentState(newWheelState);
        currentWheelKey.addWheelState(newWheelState);
        currentWheelKey.addWheelState(currentWheelState);
        newWheelState.setPreviousState(currentWheelState);
        return currentWheelKey;
    }

    public Wheel convert(final WheelKey wheelKey)
    {
        final WheelState wheelState = wheelKey.getCurrentState();
        return convert(wheelState);
    }

    public Wheel convert(final WheelState wheelState)
    {
        return new Wheel(
                wheelState.getExternalId(),
                wheelState.getName(),
                wheelState.getDescription(),
                wheelState.getFrom(),
                wheelState.getTo());
    }
}
