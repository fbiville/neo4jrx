package uk.co.clarrobltd.neo4jrx;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.clarrobltd.neo4jrx.domain.BikeRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataNeo4jTest()
class BikeRepositoryQueryCheckTest
{
    private static final Logger logger = LoggerFactory.getLogger(BikeRepositoryQueryCheckTest.class);

    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j()
    {

        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
    }

    @SuppressWarnings("unused")
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
    void shouldRunReadQueriesFromAnnotationsToProveTheyAreValid(@Autowired Neo4jClient neo4jClient)
    {
        for (final Method method : BikeRepository.class.getDeclaredMethods())
        {
            logger.info("Checking method: " + method.getName());
            final Query queryAnnotation = method.getDeclaredAnnotation(Query.class);
            if (queryAnnotation == null)
            {
                continue;
            }
            final String query = queryAnnotation.value();
            logger.warn("Testing query: \n" + query);
            final Collection<Map<String, Object>> results = neo4jClient
                    .query(query)
                    .bind("Roubaix")
                    .to("externalId")
                    .fetch()
                    .all();
            logger.warn("RESULT SUMMARY: {}", results);
            results.stream()
                   .flatMap(stringObjectMap -> stringObjectMap.entrySet().stream())
                   .forEach(entry -> {
                       final String name = entry.getKey();
                       final Object result = entry.getValue();
                       final Class<?> resultClass = result.getClass();
                       logger.warn("result name = {}, type = {}", name, resultClass.getName());
                       if (result instanceof Path)
                       {
                           logPath((Path) result);
                       }
                       else if (result instanceof Record)
                       {
                           logRecord((Record) result);
                       }
                   });
            assertThat(results.size(), is(greaterThan(0)));
        }
    }

    private void logPath(final Path path)
    {
        logger.warn("path length = {}, start labels = {}", path.length(), list(path.start().labels()));
        getNodesStream(path).forEach(node -> logger.info(node(node)));
        getRelationshipsStream(path).forEach(relationship -> logger.info(relationship(relationship)));
    }

    private String node(final Node node)
    {
        return "Node(" + node.id() + " " + list(node.labels()) + ")";
    }

    private String relationship(final Relationship relationship)
    {
        return "Relationship(" + relationship.id()  + " :" + relationship.type() + " (" + relationship.startNodeId() + ")->(" + relationship.endNodeId() + "))";
    }

    private List<String> list(final Iterable<String> iterable)
    {
        return stream(iterable.spliterator(), false).collect(toList());
    }

    private Stream<Node> getNodesStream(final Path path)
    {
        return stream(path.nodes().spliterator(), false);
    }

    private Stream<Relationship> getRelationshipsStream(final Path path)
    {
        return stream(path.relationships().spliterator(), false);
    }

    private void logRecord(final Record record)
    {
        final List<String> keys = record.keys();
        logger.info("Record 0 has keys: " + keys);
        for (String key : keys)
        {
            final Value value = record.get(key);
            logValue(key, value);
        }
    }

    private void logValue(final String key, final Value value)
    {
        if (value.type() == InternalTypeSystem.TYPE_SYSTEM.NODE())
        {
            logger.info("value for key: " + key + " is a node");
            final Node node = value.asNode();
            node.labels().forEach(s -> logger.info("label: {}", s));
            node.asMap().forEach((s, o) -> logger.info("key: {}, value: {}", s, o));
        }
        else if (value.type() == InternalTypeSystem.TYPE_SYSTEM.LIST())
        {
            logger.info("value for key: " + key + " is a list");
        }
        else if (value.type() == InternalTypeSystem.TYPE_SYSTEM.PATH())
        {
            logger.info("value for key: " + key + " is a path");
        }
        else if (value.type() == InternalTypeSystem.TYPE_SYSTEM.RELATIONSHIP())
        {
            logger.info("value for key: " + key + " is a relationship");
        }
        else if (value.type() == InternalTypeSystem.TYPE_SYSTEM.LOCAL_DATE_TIME())
        {
            logger.info("value for key: " + key + " is a local date time");
        }
        else if (value.type() == InternalTypeSystem.TYPE_SYSTEM.STRING())
        {
            logger.info("value for key: " + key + " is a String with value " + value.asString());
        }
        else
        {
            logger.info("value of type: " + value.type());
            logger.info("value for key: " + key + " = " + value.asString());
        }
    }
}
