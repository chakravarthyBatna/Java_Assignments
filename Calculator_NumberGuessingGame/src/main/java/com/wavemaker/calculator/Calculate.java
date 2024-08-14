package com.wavemaker.calculator;

import java.math.BigDecimal;

public class Calculate {

    public BigDecimal addNum(BigDecimal numOne, BigDecimal numTwo) {
        return numOne.add(numTwo);
    }


    public BigDecimal subNum(BigDecimal numOne, BigDecimal numTwo) {
        return numOne.subtract(numTwo);
    }


    public BigDecimal mulNum(BigDecimal numOne, BigDecimal numTwo) {
        return numOne.multiply(numTwo);
    }


    public BigDecimal divNum(BigDecimal numOne, BigDecimal numTwo) {
        return numOne.divide(numTwo);
    }
}
