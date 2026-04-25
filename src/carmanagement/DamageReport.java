package carmanagement;

public class DamageReport {
    private final String reportId;
    private final String reservationId;
    private final String notes;
    private final double fee;

    public DamageReport(String reportId, String reservationId, String notes, double fee) {
        this.reportId = reportId;
        this.reservationId = reservationId;
        this.notes = notes;
        this.fee = fee;
    }

    public String getReportId() {
        return reportId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getNotes() {
        return notes;
    }

    public double getFee() {
        return fee;
    }

    public String toFileRecord() {
        return String.join("|", reportId, reservationId, notes, String.valueOf(fee));
    }

    @Override
    public String toString() {
        return reportId + " | Reservation: " + reservationId + " | Fee: $" + String.format("%.2f", fee);
    }
}
