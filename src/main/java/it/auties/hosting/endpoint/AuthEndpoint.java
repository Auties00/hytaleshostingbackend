package it.auties.hosting.endpoint;

import it.auties.hosting.entity.User;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.auties.hosting.repository.UserRepository;
import it.auties.hosting.utils.TokenUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

@Path("/api/auth")
@Consumes(MediaType.TEXT_PLAIN)
public class AuthEndpoint {
    @Autowired
    UserRepository userRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @POST
    @PermitAll
    @Path("/register")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response register(@HeaderParam String username, @HeaderParam String password) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        if(username == null || password == null){
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Please provide a non null username and password")
                    .build();
        }

        if(userRepository.findUserByUsername(username) != null){
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("This username already exists!")
                    .build();
        }

        var user = new User(username, BCrypt.hashpw(password, BCrypt.gensalt(15)), "user");
        userRepository.save(user);
        return Response
                .status(Response.Status.CREATED)
                .entity(TokenUtils.generateToken(user.getUsername(), Set.of(user.getRole()), 3600L, issuer))
                .build();
    }

    @POST
    @PermitAll
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response login(@HeaderParam String username, @HeaderParam String password) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        System.out.println(issuer);
        if(username == null || password == null){
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Please provide a non null username and password")
                    .encoding(StandardCharsets.UTF_8.name())
                    .build();
        }

        var user = userRepository.findUserByUsername(username);
        if(user == null){
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("The username provided doesn't exist")
                    .build();
        }

        return BCrypt.checkpw(password, user.getPassword()) ? Response.status(Response.Status.OK).entity(TokenUtils.generateToken(user.getUsername(), Set.of(user.getRole()), 3600L, issuer)).build() : Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect Password").build();
    }
}
