package it.auties.hosting.main;

import com.stripe.Stripe;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class HytalesServerApplicationEvent {
    @ConfigProperty(name = "it.auties.stripe.apiKey")
    private String stripeApiKey;

    void onStart(@Observes StartupEvent ev) {
        Stripe.apiKey = stripeApiKey;
    }
}