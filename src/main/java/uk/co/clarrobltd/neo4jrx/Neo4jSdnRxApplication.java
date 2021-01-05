package uk.co.clarrobltd.neo4jrx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EntityScan(basePackages = "uk.co.clarrobltd.neo4jrx")
@EnableNeo4jRepositories(basePackages = "uk.co.clarrobltd.neo4jrx")
public class Neo4jSdnRxApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(Neo4jSdnRxApplication.class, args);
    }
}
