package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Component
public class BikeTransformer
{
    private WheelTransformer wheelTransformer;

    @Autowired
    public void setWheelTransformer(final WheelTransformer wheelTransformer)
    {
        this.wheelTransformer = wheelTransformer;
    }

    public BikeKey convertToCreate(final Bike bike, final BikeRelatedState bikeRelatedState)
    {
        final BikeState bikeState = new BikeState(
                bike.getExternalId(),
                bike.getName(),
                bike.getDescription(),
                LocalDateTime.now(),
                LocalDateTime.parse("3000-01-01T00:00"));
        bikeState.setWheels(bikeRelatedState.getWheels());
        return new BikeKey(
                bike.getExternalId(),
                bikeState,
                singletonList(bikeState));
    }

    public BikeKey convertToUpdate(
            final Bike newBike,
            final BikeKey currentBikeKey,
            final BikeRelatedState bikeRelatedState)
    {
        final BikeState currentBikeState = currentBikeKey.getCurrentState();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final BikeState newBikeState = new BikeState(
                newBike.getExternalId(),
                newBike.getName(),
                newBike.getDescription(),
                localDateTime,
                LocalDateTime.parse("3000-01-01T00:00"));
        currentBikeState.setTo(localDateTime);
        currentBikeKey.setCurrentState(newBikeState);
        currentBikeKey.addBikeState(newBikeState);
        currentBikeKey.addBikeState(currentBikeState);
        newBikeState.setPreviousState(currentBikeState);
        newBikeState.setWheels(bikeRelatedState.getWheels());
        return currentBikeKey;
    }

    public Bike convert(final BikeKey bikeKey)
    {
        return convert(bikeKey.getCurrentState());
    }

    public Bike convert(final BikeState bikeState)
    {
        return new Bike(
                bikeState.getExternalId(),
                bikeState.getName(),
                bikeState.getDescription(),
                establishWheels(bikeState.getWheels()),
                bikeState.getFrom(),
                bikeState.getTo());
    }

    private List<Wheel> establishWheels(final List<WheelState> wheelStates)
    {
        if (wheelStates == null)
        {
            return null;
        }
        return wheelStates
                .stream()
                .map(wheelTransformer::convert)
                .collect(toList());
    }
}
