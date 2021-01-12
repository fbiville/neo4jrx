package uk.co.clarrobltd.neo4jrx;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.co.clarrobltd.neo4jrx.domain.BikeKey;
import uk.co.clarrobltd.neo4jrx.domain.BikeRepository;
import uk.co.clarrobltd.neo4jrx.domain.BikeState;

import java.io.IOException;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataNeo4jTest
class BikeRepositoryTest {
    private static Neo4j embeddedDatabaseServer;

    @Autowired
    private BikeRepository bikeRepository;

    @BeforeAll
    static void initializeNeo4j() {

        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .build();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> null);
    }

    @AfterAll
    static void stopNeo4j() {
        embeddedDatabaseServer.close();
    }

    @BeforeEach
    void setup(@Autowired Neo4jClient neo4jClient) {
        neo4jClient.query("MATCH (n) DETACH DELETE n;").run();
        neo4jClient.query("CREATE\n" +
                "  (bike:Bike{\n" +
                "    externalId:'Roubaix'\n" +
                "  }),\n" +
                "  (state:BikeState{\n" +
                "    externalId:'Roubaix',\n" +
                "    name:\"Robert's Roubaix\",\n" +
                "    description:'2010 Roubaix with Tiagra and Fulcrum Racing 4s'\n" +
                "  }),\n" +
                "  (bike)-[:CURRENT_STATE]->(state)").run();
    }

    @Test
    @DisplayName("Exhibit issue when mapping path to single entity")
    // https://github.com/spring-projects/spring-data-neo4j/issues/2107
    void shouldReadCurrentBikeUsingPath() {
        BikeKey expected = getExpectedCurrentRoubaix();

        BikeKey actual = bikeRepository.getCurrentBikeUsingPath(expected.getExternalId());

        assertThat(actual.getExternalId(), equalTo(expected.getExternalId()));
    }

    @Test
    @DisplayName("Exhibit hydration issue when mapping path")
    // https://github.com/spring-projects/spring-data-neo4j/issues/2109
    void shouldReadCurrentBikesUsingPath() {
        BikeKey expected = getExpectedCurrentRoubaix();

        Set<BikeKey> actual = bikeRepository.getCurrentBikesUsingPath(expected.getExternalId());

        assertThat(actual, hasSize(1));
        assertEqual(actual.iterator().next(), expected);
    }

    private static void assertEqual(final BikeKey actual, final BikeKey expected) {
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        assertThat("currentState should be hydrated", actual.getCurrentState(), is(notNullValue()));
        assertEqual(actual.getCurrentState(), expected.getCurrentState());
    }

    private static void assertEqual(final BikeState actual, final BikeState expected) {
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        assertThat(actual.getName(), is(equalTo(expected.getName())));
        assertThat(actual.getDescription(), is(equalTo(expected.getDescription())));
    }

    private BikeState getCurrentRoubaixState() {
        return new BikeState(
                "Roubaix",
                "Robert's Roubaix",
                "2010 Roubaix with Tiagra and Fulcrum Racing 4s"
        );
    }

    private BikeKey getExpectedCurrentRoubaix() {
        final BikeState currentState = getCurrentRoubaixState();
        return new BikeKey("Roubaix", currentState);
    }
}