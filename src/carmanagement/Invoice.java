package carmanagement;

public class Invoice {
    private final String invoiceId;
    private final String reservationId;
    private final double baseAmount;
    private final double discountAmount;
    private final double damageFee;
    private final double mileageFee;
    private final double totalAmount;

    public Invoice(String invoiceId, String reservationId, double baseAmount, double discountAmount,
            double damageFee, double mileageFee, double totalAmount) {
        this.invoiceId = invoiceId;
        this.reservationId = reservationId;
        this.baseAmount = baseAmount;
        this.discountAmount = discountAmount;
        this.damageFee = damageFee;
        this.mileageFee = mileageFee;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getDamageFee() {
        return damageFee;
    }

    public double getMileageFee() {
        return mileageFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String toFileRecord() {
        return String.join("|",
                invoiceId,
                reservationId,
                String.valueOf(baseAmount),
                String.valueOf(discountAmount),
                String.valueOf(damageFee),
                String.valueOf(mileageFee),
                String.valueOf(totalAmount));
    }

    @Override
    public String toString() {
        return invoiceId + " | Reservation: " + reservationId + " | Total: $" + String.format("%.2f", totalAmount);
    }
}
