package uk.co.clarrobltd.neo4jrx.domain;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BikeRepository extends Neo4jRepository<BikeKey, Long> {

    @Query("MATCH path=(bike:Bike {externalId: $externalId})-[:CURRENT_STATE]->(:BikeState) RETURN path")
    BikeKey getCurrentBikeUsingPath(@Param("externalId") final String externalId);

    @Query("MATCH path=(bike:Bike {externalId: $externalId})-[:CURRENT_STATE]->(:BikeState) RETURN path")
    Set<BikeKey> getCurrentBikesUsingPath(@Param("externalId") final String externalId);
}
