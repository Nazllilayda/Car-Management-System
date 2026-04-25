package carmanagement;

public class Customer {
    private final String customerId;
    private final String fullName;
    private final String phoneNumber;
    private final String licenseNumber;
    private int loyaltyPoints;
    private LoyaltyTier loyaltyTier;

    public Customer(String customerId, String fullName, String phoneNumber, String licenseNumber, int loyaltyPoints) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.loyaltyPoints = loyaltyPoints;
        this.loyaltyTier = LoyaltyTier.fromPoints(loyaltyPoints);
    }

    // Method overloading: simplified registration path delegates to the detailed constructor.
    public Customer(String customerId, String fullName, String phoneNumber, String licenseNumber) {
        this(customerId, fullName, phoneNumber, licenseNumber, 0);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public void addLoyaltyPoints(int points) {
        loyaltyPoints += points;
        loyaltyTier = LoyaltyTier.fromPoints(loyaltyPoints);
    }

    public double getDiscountRate() {
        return loyaltyTier.getDiscountRate();
    }

    public String toFileRecord() {
        return String.join("|",
                customerId,
                fullName,
                phoneNumber,
                licenseNumber,
                String.valueOf(loyaltyPoints));
    }

    @Override
    public String toString() {
        return customerId + " - " + fullName + " | Tier: " + loyaltyTier + " | Points: " + loyaltyPoints;
    }
}
