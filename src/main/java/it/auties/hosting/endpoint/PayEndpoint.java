package it.auties.hosting.endpoint;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import it.auties.hosting.entity.Server;
import it.auties.hosting.model.Location;
import it.auties.hosting.model.Plan;
import it.auties.hosting.repository.ServerRepository;
import it.auties.hosting.repository.UserRepository;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.concurrent.ExecutionException;

@Path("/api/pay")
@Consumes(MediaType.TEXT_PLAIN)
public class PayEndpoint {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ServerRepository serverRepository;

    @POST
    @Path("/completePayment")
    @RolesAllowed("user")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pay(@Context SecurityContext securityContext, @HeaderParam String id, @HeaderParam String planHeader, @HeaderParam String locationHeader) throws StripeException, ExecutionException, InterruptedException {
        if(planHeader == null || locationHeader == null){
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("The plan and location must be non null")
                    .build();
        }

        var plan = Plan.anyMatchOrThrow(planHeader);
        var location = Location.anyMatchOrThrow(locationHeader);
        var params = new PaymentIntentCreateParams.Builder()
                .setCurrency("EUR")
                .setAmount((long) plan.getPrice())
                .setPaymentMethod(id)
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
                .setConfirm(true)
                .build();

        var intent = PaymentIntent.create(params);
        if (intent.getStatus().equals("succeeded")) {
            var server = new Server(location, plan);
            serverRepository.save(server);
            var user = userRepository.findUserByUsername(securityContext.getUserPrincipal().getName());
            if (user == null) {
                throw new SecurityException("The user doesn't exist");
            }

            user.getServers().add(server);
            userRepository.save(user);
            return Response
                    .ok("The plan was successfully purchased")
                    .build();
        }

        return Response
                .status(Response.Status.NOT_ACCEPTABLE)
                .entity("The purchase was cancelled because of an unknown error")
                .build();
    }
}
