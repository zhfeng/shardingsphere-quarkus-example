package org.apache.shardingsphere.example;

import io.quarkus.arc.Arc;

import javax.enterprise.inject.literal.NamedLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Path("/users")
public class ShardingSphereQuarkusExample {

    @Inject
    EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT a FROM User a", User.class).getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Path("/{ds}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Integer countUsers(@PathParam("ds") String ds) throws Exception {
        DataSource dataSource = Arc.container().instance(DataSource.class, NamedLiteral.of(ds)).get();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM t_user");
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }
}
