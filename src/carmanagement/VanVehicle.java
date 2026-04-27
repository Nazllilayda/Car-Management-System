package carmanagement;

public class VanVehicle extends Vehicle {
    public VanVehicle(String vehicleId, String plateNumber, String brand, String model, String color, int year, String branchId) {
        super(vehicleId, plateNumber, brand, model, color, year, branchId);
    }

    @Override
    public String getVehicleType() {
        return "Van";
    }

    @Override
    public double getDailyRate() {
        return 120.0;
    }

    @Override
    public int getMileagePolicy() {
        return 200;
    }
}
