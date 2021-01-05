package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BikeRepository extends Neo4jRepository<BikeKey, Long>
{
    BikeKey findByExternalId(@Param("externalId") final String externalId);

    @Query( "MATCH path =\n" +
            "  (bike:Bike)-[:CURRENT_STATE]->(:BikeState)\n" +
            "WHERE\n" +
            "  bike.externalId = $externalId\n" +
            "RETURN\n" +
            "  path;")
    BikeKey getCurrentBikeUsingPath(@Param("externalId") final String externalId);

    @Query( "MATCH path =\n" +
            "  (bike:Bike)-[:CURRENT_STATE]->(bikeState:BikeState)\n" +
            "WHERE\n" +
            "  bike.externalId = $externalId\n" +
            "OPTIONAL MATCH relationsPath =\n" +
            "  (bikeState)-[]-(relatedState)\n" +
            "WHERE\n" +
            "  relatedState.from <= localdatetime('3000-01-01T00:00') < relatedState.to\n" +
            "RETURN\n" +
            "  path,\n" +
            "  relationsPath;")
    BikeKey getCurrentBikeAndWheelOgmStyle(@Param("externalId") final String externalId);

    @Query( "MATCH\n" +
            "  (bike:Bike)-[currentStateRelationship:CURRENT_STATE]->(bikeState:BikeState)\n" +
            "WHERE\n" +
            "  bike.externalId = $externalId\n" +
            "RETURN\n" +
            "  bike,\n" +
            "  currentStateRelationship,\n" +
            "  bikeState;")
    BikeKey getCurrentBikeOnly(@Param("externalId") final String externalId);

    @Query( "MATCH\n" +
            "  (bike:Bike)-[currentStateRelationship:CURRENT_STATE]->(bikeState:BikeState)\n" +
            "WHERE\n" +
            "  bike.externalId = $externalId\n" +
            "RETURN\n" +
            "  bike,\n" +
            "  collect(currentStateRelationship),\n" +
            "  collect(bikeState);")
    BikeKey getCurrentBikeOnlyUsingCollect(@Param("externalId") final String externalId);

    @Query( "MATCH\n" +
            "  (bike:Bike)-[currentStateRelationship:CURRENT_STATE]->(bikeState:BikeState)\n" +
            "WHERE\n" +
            "  bike.externalId = $externalId\n" +
            "OPTIONAL MATCH\n" +
            "  (bikeState)-[relatedStateRelationship]->(relatedState)\n" +
            "WHERE\n" +
            "  relatedState.from <= localdatetime('3000-01-01T00:00') < relatedState.to\n" +
            "RETURN\n" +
            "  bike,\n" +
            "  collect(currentStateRelationship),\n" +
            "  collect(bikeState),\n" +
            "  collect(relatedStateRelationship),\n" +
            "  collect(relatedState);")
    BikeKey getCurrentBikeAndRelatedData(@Param("externalId") final String externalId);
}
