package it.auties.hosting.model;

import lombok.NonNull;

import java.util.Arrays;

public enum Location {
    AMERICA,
    NETHERLANDS,
    GERMANY,
    FRANCE,
    UK,
    SWEDEN;

    public static Location anyMatchOrThrow(@NonNull String location) {
        return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(location)).findFirst().orElseThrow(() -> new RuntimeException("Please provide a valid location"));
    }
}
