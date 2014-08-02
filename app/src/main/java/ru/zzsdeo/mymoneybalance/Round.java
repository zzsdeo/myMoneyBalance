package ru.zzsdeo.mymoneybalance;

import java.math.BigDecimal;

public class Round {

    public Round() {
        // TODO Auto-generated constructor stub
    }

    public static double roundedDouble(double d) {
        return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
