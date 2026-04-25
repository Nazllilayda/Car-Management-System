package carmanagement;

public class LuxuryVehicle extends Vehicle {
    public LuxuryVehicle(String vehicleId, String plateNumber, String brand, String model, int year, String branchId) {
        super(vehicleId, plateNumber, brand, model, year, branchId);
    }

    @Override
    public String getVehicleType() {
        return "Luxury";
    }

    @Override
    public double getDailyRate() {
        return 180.0;
    }

    @Override
    public int getMileagePolicy() {
        return 180;
    }

    @Override
    public double calculateBaseRental(java.time.LocalDate startDate, java.time.LocalDate endDate, InsuranceOption insuranceOption) {
        return super.calculateBaseRental(startDate, endDate, insuranceOption) + 35.0;
    }
}
