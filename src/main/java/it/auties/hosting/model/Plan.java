package it.auties.hosting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

@AllArgsConstructor
public enum Plan {
    STONE(799),
    COAL(1499),
    GOLD(1999),
    REDSTONE(2999),
    DIAMOND(3999),
    EMERALD(4999);

    @Getter
    private final int price;

    public static Plan anyMatchOrThrow(@NonNull String plan) {
        return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(plan)).findFirst().orElseThrow(() -> new RuntimeException("Please provide a valid plan"));
    }
}
