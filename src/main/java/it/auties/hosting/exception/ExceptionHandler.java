package it.auties.hosting.exception;

import it.auties.hosting.endpoint.AuthEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {
    private final Logger logger = LoggerFactory.getLogger(AuthEndpoint.class);

    @Override
    public Response toResponse(Exception exception) { 
        logger.error(String.format("Caught an Exception of type %s with message %s!", exception.getClass(), exception.getMessage()));
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("There was an error handling your request" + (exception.getMessage() == null ? "" : ". Error: " + exception.getMessage()))
                .build();
    }
}

