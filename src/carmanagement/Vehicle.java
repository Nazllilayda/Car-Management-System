package carmanagement;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Abstraction + Inheritance: concrete vehicle types specialize this shared rental model.
public abstract class Vehicle {
    private final String vehicleId;
    private final String plateNumber;
    private final String brand;
    private final String model;
    private final String color;
    private final int year;
    private final String branchId;
    private VehicleStatus status;

    protected Vehicle(String vehicleId, String plateNumber, String brand, String model, String color, int year, String branchId) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.year = year;
        this.branchId = branchId;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public int getYear() {
        return year;
    }

    public String getBranchId() {
        return branchId;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public abstract String getVehicleType();

    public abstract double getDailyRate();

    public abstract int getMileagePolicy();

    // Polymorphism: the runtime vehicle type supplies the actual rate and policy values.
    public double calculateBaseRental(LocalDate startDate, LocalDate endDate, InsuranceOption insuranceOption) {
        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        return (days * getDailyRate()) + (days * insuranceOption.getDailyFee());
    }

    public String toFileRecord() {
        return String.join("|",
                getVehicleType(),
                vehicleId,
                plateNumber,
                brand,
                model,
                color,
                String.valueOf(year),
                branchId,
                status.name());
    }

    @Override
    public String toString() {
        return vehicleId + " - " + getVehicleType() + " - " + brand + " " + model
                + " (" + color + ", " + year + "), Plate: " + plateNumber
                + ", Branch: " + branchId + ", Status: " + status;
    }
}
