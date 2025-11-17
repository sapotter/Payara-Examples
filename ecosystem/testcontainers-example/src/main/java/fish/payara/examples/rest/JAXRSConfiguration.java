package fish.payara.examples.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/resources")
public class JAXRSConfiguration extends Application {
    // No code needed — this activates JAX-RS at /resources/*
}
