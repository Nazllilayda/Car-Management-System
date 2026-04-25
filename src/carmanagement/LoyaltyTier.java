package carmanagement;

public enum LoyaltyTier {
    BRONZE(0, 0.00),
    SILVER(200, 0.05),
    GOLD(500, 0.10);

    private final int minimumPoints;
    private final double discountRate;

    LoyaltyTier(int minimumPoints, double discountRate) {
        this.minimumPoints = minimumPoints;
        this.discountRate = discountRate;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public static LoyaltyTier fromPoints(int points) {
        if (points >= GOLD.minimumPoints) {
            return GOLD;
        }
        if (points >= SILVER.minimumPoints) {
            return SILVER;
        }
        return BRONZE;
    }
}
