package carmanagement;

import java.time.LocalDate;

public class MaintenanceRecord {
    private final String maintenanceId;
    private final String vehicleId;
    private final String mechanicId;
    private final LocalDate scheduledDate;
    private final String description;

    public MaintenanceRecord(String maintenanceId, String vehicleId, String mechanicId, LocalDate scheduledDate, String description) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.mechanicId = mechanicId;
        this.scheduledDate = scheduledDate;
        this.description = description;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public String getDescription() {
        return description;
    }

    public String toFileRecord() {
        return String.join("|", maintenanceId, vehicleId, mechanicId, scheduledDate.toString(), description);
    }

    @Override
    public String toString() {
        return maintenanceId + " | Vehicle: " + vehicleId + " | Mechanic: " + mechanicId
                + " | Date: " + scheduledDate + " | " + description;
    }
}
