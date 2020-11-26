package uk.co.clarrobltd.neo4jrx;

import org.neo4j.springframework.data.repository.config.EnableNeo4jRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

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
