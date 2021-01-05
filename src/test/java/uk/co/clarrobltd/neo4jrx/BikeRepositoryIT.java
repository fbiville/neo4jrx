package uk.co.clarrobltd.neo4jrx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.co.clarrobltd.neo4jrx.domain.BikeKey;
import uk.co.clarrobltd.neo4jrx.domain.BikeRepository;
import uk.co.clarrobltd.neo4jrx.domain.BikeState;
import uk.co.clarrobltd.neo4jrx.domain.WheelKey;
import uk.co.clarrobltd.neo4jrx.domain.WheelState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

//@RunWith(SpringRunner.class)
@DataNeo4jTest()
@TestMethodOrder(OrderAnnotation.class)
class BikeRepositoryIT
{
    private static final Logger logger = LoggerFactory.getLogger(BikeRepositoryIT.class);

    private static Neo4j embeddedDatabaseServer;

    @Autowired
    private BikeRepository bikeRepository;

    @BeforeAll
    static void initializeNeo4j()
    {

        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry)
    {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> null);
    }

    @AfterAll
    static void stopNeo4j()
    {
        embeddedDatabaseServer.close();
    }

    @BeforeEach
    void setup(@Autowired Neo4jClient neo4jClient) throws IOException
    {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/bikes-test-data.cypher"))))
        {
            final String bikesCypher = bufferedReader.lines().collect(joining(" "));
            neo4jClient.query("MATCH (n) DETACH DELETE n;").run();
            neo4jClient.query(bikesCypher).run();
        }
    }

    @Test
    @Order(1)
    void shouldReadBikeUsingFindAll() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedRoubaixFull();
        // act
        final List<BikeKey> actual = bikeRepository.findAll();
        // assert
        assertThat(actual, is(notNullValue()));
        assertThat(actual.size(), is(equalTo(2)));
        assertEqual(actual.stream().filter(bikeKey -> bikeKey.getExternalId().equals("Roubaix")).findFirst().orElse(null), expected);
    }

    @Test
    @Order(2)
    void shouldReadBikeUsingFindByExternalId() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedRoubaixFull();
        // act
        final BikeKey actual = bikeRepository.findByExternalId("Roubaix");
        // assert
        assertEqual(actual, expected);
    }

    @Test
    @Order(3)
    @DisplayName("Simple query returning one item (:Key)-[CURRENT_STATE]->(:State) using single path")
    void shouldReadCurrentBikeUsingPath() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedCurrentRoubaix();
        // act
        final BikeKey actual = bikeRepository.getCurrentBikeUsingPath(expected.getExternalId());
        // assert
        assertEqual(actual, expected);
    }

    @Test
    @Order(4)
    @DisplayName("Deeper query returning one item and related state (:Key)-[CURRENT_STATE]->(:State)-[]-(relatedState) using two paths")
    void shouldReadCurrentBikeAndWheelOgmStyle() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedCurrentRoubaix();
        // act
        final BikeKey actual = bikeRepository.getCurrentBikeAndWheelOgmStyle(expected.getExternalId());
        // assert
        assertEqual(actual, expected);
    }

    @Test
    @Order(5)
    @DisplayName("Would expect this to work as only making one connection")
    void shouldReadBikeUsingGetBikeOnly() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedCurrentRoubaixOnly();
        // act
        final BikeKey actual = bikeRepository.getCurrentBikeOnly(expected.getExternalId());
        // assert
        assertEqual(actual, expected);
    }

    @Test
    @Order(6)
    @DisplayName("Using collect() makes (:Key)-[:CURRENT_STATE)->(:State) work")
    void shouldReadBikeUsingGetBikeOnlyUsingCollect() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedCurrentRoubaixOnly();
        // act
        final BikeKey actual = bikeRepository.getCurrentBikeOnlyUsingCollect(expected.getExternalId());
        // assert
        assertEqual(actual, expected);
    }

    @Test
    @Order(7)
    void shouldReadBikeUsingGetCurrentBikeAndRelatedData() throws JsonProcessingException
    {
        // arrange
        final BikeKey expected = getExpectedRoubaix();
        // act
        final BikeKey actual = bikeRepository.getCurrentBikeAndRelatedData(expected.getExternalId());
        // assert
        assertEqual(actual, expected);
    }

    //====================================================================================================
    // Equality Tests
    //====================================================================================================
    private static void assertEqual(final BikeKey actual, final BikeKey expected) throws JsonProcessingException
    {
        logValue(actual, "Actual BikeKey (top level)");
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        if (expected.getCurrentState() != null)
        {
            assertThat("currentState should not be null", actual.getCurrentState(), is(notNullValue()));
            assertEqual(actual.getCurrentState(), expected.getCurrentState());
        }
        if (expected.getBikeStates() != null && !expected.getBikeStates().isEmpty())
        {
            assertThat("bikeStates should not be null", actual.getBikeStates(), is(notNullValue()));
            assertThat("bikeStates does not have the expected number of items", actual.getBikeStates().size(), is(equalTo(expected.getBikeStates().size())));
            actual.getBikeStates().sort(comparing(BikeState::getTo));
            expected.getBikeStates().sort(comparing(BikeState::getTo));
            for (int index = 0; index < expected.getBikeStates().size(); index++)
            {
                assertEqual(actual.getBikeStates().get(index), expected.getBikeStates().get(index));
            }
        }
    }

    private static void assertEqual(final WheelKey actual, final WheelKey expected) throws JsonProcessingException
    {
        logValue(actual, "Actual WheelKey");
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        if (expected.getCurrentState() != null)
        {
            assertThat("currentState should not be null", actual.getCurrentState(), is(notNullValue()));
            assertEqual(actual.getCurrentState(), expected.getCurrentState());
        }
    }

    private static void assertEqual(final BikeState actual, final BikeState expected) throws JsonProcessingException
    {
        logValue(actual, "Actual BikeState");
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        assertThat(actual.getName(), is(equalTo(expected.getName())));
        assertThat(actual.getDescription(), is(equalTo(expected.getDescription())));
        assertThat(actual.getFrom(), is(equalTo(expected.getFrom())));
        assertThat(actual.getTo(), is(equalTo(expected.getTo())));
        if (expected.getWheels() != null && !expected.getWheels().isEmpty())
        {
            assertThat("wheels should not be null", actual.getWheels(), is(notNullValue()));
            assertThat("wheels does not have the expected number of items", actual.getWheels().size(), is(equalTo(expected.getWheels().size())));
            actual.getWheels().sort(comparing(WheelState::getTo));
            expected.getWheels().sort(comparing(WheelState::getTo));
            for (int index = 0; index < expected.getWheels().size(); index++)
            {
                assertEqual(actual.getWheels().get(index), expected.getWheels().get(index));
            }
        }
    }

    private static void assertEqual(final WheelState actual, final WheelState expected)
    {
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        assertThat(actual.getName(), is(equalTo(expected.getName())));
        assertThat(actual.getDescription(), is(equalTo(expected.getDescription())));
        assertThat(actual.getFrom(), is(equalTo(expected.getFrom())));
        assertThat(actual.getTo(), is(equalTo(expected.getTo())));
    }

    //====================================================================================================
    // Expected data
    //====================================================================================================
    private BikeState getCurrentRoubaixState()
    {
        return new BikeState(
                "Roubaix",
                "Robert's Roubaix",
                "2010 Roubaix with Tiagra and Fulcrum Racing 4s",
                LocalDateTime.parse("2020-06-08T08:52:20.123456"),
                LocalDateTime.parse("9999-12-31T23:59:59.999"));
    }

    private BikeState getCurrentRoubaixStateWithWheels()
    {
        final BikeState currentState = getCurrentRoubaixState();
        currentState.setWheels(singletonList(getExpectedFulcrum4().getCurrentState()));
        return currentState;
    }

    private BikeKey getExpectedCurrentRoubaixOnly()
    {
        final BikeState currentState = getCurrentRoubaixState();
        return new BikeKey("Roubaix", currentState, null);
    }

    private BikeKey getExpectedRoubaix()
    {
        final BikeState currentState = getCurrentRoubaixStateWithWheels();
        return new BikeKey("Roubaix", currentState, singletonList(currentState));
    }

    private BikeKey getExpectedCurrentRoubaix()
    {
        final BikeState currentState = getCurrentRoubaixStateWithWheels();
        return new BikeKey("Roubaix", currentState, null);
    }

    private BikeKey getExpectedRoubaixFull()
    {
        final BikeState currentState = getCurrentRoubaixState();
        final BikeState previousState = new BikeState(
                "Roubaix",
                "Robert's Roubaix",
                "2010 Roubaix with Tiagra and Fulcrum Racing 4s - previous",
                LocalDateTime.parse("2020-06-07T08:52:20.123456"),
                LocalDateTime.parse("2020-06-08T08:52:20.123456"));
        currentState.setWheels(singletonList(getExpectedFulcrum4().getCurrentState()));
        return new BikeKey("Roubaix", currentState, asList(currentState, previousState));
    }

    private WheelKey getExpectedFulcrum4()
    {
        final WheelState currentState = new WheelState(
                "Fulcrum4",
                "Fulcrum Racing 4",
                "2019 Fulcrum Racing 4s with GP5000s",
                LocalDateTime.parse("2020-06-08T08:52:20.123456"),
                LocalDateTime.parse("9999-12-31T23:59:59.999"));
        return new WheelKey("Fulcrum4", currentState, singletonList(currentState));
    }

    //====================================================================================================
    // Logging as multi-line JSON
    //====================================================================================================
    private static void logValue(final Object value, final String title) throws JsonProcessingException
    {
        final String prefix = "\n" +
                "----------------------------------------------------------------------------------------------------\n" +
                "- " + title + " -\n" +
                "----------------------------------------------------------------------------------------------------\n";
        logger.info("{}{}", prefix, new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValueAsString(value));
    }
}