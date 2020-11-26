package uk.co.clarrobltd.neo4jrx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.types.Node;
import org.neo4j.springframework.boot.test.autoconfigure.data.DataNeo4jTest;
import org.neo4j.springframework.data.repository.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.clarrobltd.neo4jrx.domain.BikeRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataNeo4jTest()
class BikeRepositoryQueryCheckTest
{
    private static final Logger logger = LoggerFactory.getLogger(BikeRepositoryQueryCheckTest.class);

    @Autowired
    private BikeRepository bikeRepository;

    @Qualifier("neo4jDriver")
    @Autowired
    private Driver driver;

    @BeforeEach
    void setup() throws IOException
    {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/bikes-test-data.cypher")));
             final Session session = driver.session())
        {
            session.run("MATCH (n) DETACH DELETE n");
            final String bikesCypher = bufferedReader.lines().collect(joining(" "));
            session.run(bikesCypher);
        }
    }

    @Test
    void shouldRunReadQueriesFromAnnotationsToProveTheyAreValid() throws Exception
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
            try (final Session session = driver.session())
            {
                session.readTransaction(transaction -> {
                    final Result result = transaction.run(query, Values.parameters("externalId", "Roubaix"));
                    final List<Record> records = result.list();
                    assertThat(records.size(), is(greaterThan(0)));
                    logger.info("Got back " + records.size() + " record(s)");
                    final Record record = records.get(0);
                    logRecord(record);
                    return true;
                });
            }
        }
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
