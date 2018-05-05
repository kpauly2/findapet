package tech.pauly.findapet.data;

import tech.pauly.findapet.BuildConfig;

public class PetfinderEndpoints {
    public static String getEndpoint() {
        String environment = BuildConfig.ENVIRONMENT;
        switch (environment) {
            case "prod":
                return "http://api.petfinder.com/";
            case "espresso":
                return "http://localhost:8010/";
            default:
                throw new IllegalStateException("PetfinderEndpoints " + environment + " is not a supported environment");
        }
    }
}
