package model;

import java.math.BigDecimal;

public class ReportRow {
    private final String name;
    private final int count;
    private final BigDecimal totalAmount;
    private final BigDecimal totalApprovedAmount;

    public ReportRow(String name, int count, BigDecimal totalAmount, BigDecimal totalApprovedAmount) {
        this.name = name;
        this.count = count;
        this.totalAmount = totalAmount;
        this.totalApprovedAmount = totalApprovedAmount;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalApprovedAmount() {
        return totalApprovedAmount;
    }
}
