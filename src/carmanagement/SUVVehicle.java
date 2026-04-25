package carmanagement;

public class SUVVehicle extends Vehicle {
    public SUVVehicle(String vehicleId, String plateNumber, String brand, String model, int year, String branchId) {
        super(vehicleId, plateNumber, brand, model, year, branchId);
    }

    @Override
    public String getVehicleType() {
        return "SUV";
    }

    @Override
    public double getDailyRate() {
        return 95.0;
    }

    @Override
    public int getMileagePolicy() {
        return 220;
    }
}
