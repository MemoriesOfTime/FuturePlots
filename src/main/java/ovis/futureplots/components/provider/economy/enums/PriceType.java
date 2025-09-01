package ovis.futureplots.components.provider.economy.enums;

import java.util.Locale;

public enum PriceType {

    CLAIM,
    CLEAR,
    RESET,
    MERGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
