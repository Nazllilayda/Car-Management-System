package carmanagement;

import java.time.LocalDate;

public class Reservation {
    private final String reservationId;
    // Composition: a reservation is built from an existing customer and vehicle.
    private final Customer customer;
    private final Vehicle vehicle;
    private final Branch pickupBranch;
    private final Branch returnBranch;
    private final InsuranceOption insuranceOption;
    private final RentalAgent rentalAgent;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private ReservationStatus status;

    public Reservation(String reservationId, Customer customer, Vehicle vehicle, Branch pickupBranch, Branch returnBranch,
            InsuranceOption insuranceOption, RentalAgent rentalAgent, LocalDate startDate, LocalDate endDate,
            ReservationStatus status) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.pickupBranch = pickupBranch;
        this.returnBranch = returnBranch;
        this.insuranceOption = insuranceOption;
        this.rentalAgent = rentalAgent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Branch getPickupBranch() {
        return pickupBranch;
    }

    public Branch getReturnBranch() {
        return returnBranch;
    }

    public InsuranceOption getInsuranceOption() {
        return insuranceOption;
    }

    public RentalAgent getRentalAgent() {
        return rentalAgent;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String toFileRecord() {
        return String.join("|",
                reservationId,
                customer.getCustomerId(),
                vehicle.getVehicleId(),
                pickupBranch.getBranchId(),
                returnBranch.getBranchId(),
                insuranceOption.name(),
                rentalAgent.getEmployeeId(),
                startDate.toString(),
                endDate.toString(),
                status.name());
    }

    @Override
    public String toString() {
        return reservationId + " | " + customer.getFullName() + " | " + vehicle.getVehicleId()
                + " | " + startDate + " to " + endDate + " | " + status;
    }
}
