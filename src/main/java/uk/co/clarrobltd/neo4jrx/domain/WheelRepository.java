package uk.co.clarrobltd.neo4jrx.domain;

import org.neo4j.springframework.data.repository.Neo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WheelRepository extends Neo4jRepository<WheelKey, Long>
{
    @Query( "MATCH\n" +
            "  (wheel:Wheel)-[currentState:CURRENT_STATE]->(wheelState:WheelState)\n" +
            "WHERE\n" +
            "  wheel.externalId = $externalId\n" +
            "RETURN\n" +
            "  wheel,\n" +
            "  collect(currentState),\n" +
            "  collect(wheelState);")
    WheelKey getWheel(@Param("externalId") final String externalId);

    @Query( "MATCH \n" +
            "  currentPath = (wheel:Wheel)-[currentState:CURRENT_STATE]->(wheelState:WheelState)\n" +
            "RETURN\n" +
            "  wheel\n," +
            "  collect(currentState)\n," +
            "  collect(wheelState);")
    List<WheelKey> getAllWheels();

    @Query( "MATCH path =\n" +
            "  (:Wheel)-[:CURRENT_STATE|WHEEL_STATE]->(:WheelState)\n" +
            "DETACH DELETE\n" +
            "  path;")
    void deleteAllWheels();

    @Query( "MATCH\n" +
            "  (wheel:Wheel)-[currentState:CURRENT_STATE]->(wheelState:WheelState)\n" +
            "WHERE\n" +
            "  wheel.externalId IN $externalIds\n" +
            "RETURN\n" +
            "  wheelState;")
    List<WheelState> getWheels(@Param("externalIds") final List<String> externalIds);
}
