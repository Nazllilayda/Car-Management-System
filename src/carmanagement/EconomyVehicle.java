package carmanagement;

public class EconomyVehicle extends Vehicle {
    public EconomyVehicle(String vehicleId, String plateNumber, String brand, String model, String color, int year, String branchId) {
        super(vehicleId, plateNumber, brand, model, color, year, branchId);
    }

    @Override
    public String getVehicleType() {
        return "Economy";
    }

    @Override
    public double getDailyRate() {
        return 55.0;
    }

    @Override
    public int getMileagePolicy() {
        return 250;
    }
}
