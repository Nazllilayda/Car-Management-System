package carmanagement;

public enum InsuranceOption {
    BASIC(15.0),
    STANDARD(30.0),
    PREMIUM(50.0);

    private final double dailyFee;

    InsuranceOption(double dailyFee) {
        this.dailyFee = dailyFee;
    }

    public double getDailyFee() {
        return dailyFee;
    }
}
