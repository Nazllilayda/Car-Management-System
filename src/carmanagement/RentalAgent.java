package carmanagement;

public class RentalAgent extends Employee {
    public RentalAgent(String employeeId, String fullName, String branchId) {
        super(employeeId, fullName, branchId);
    }

    @Override
    public String getRole() {
        return "RentalAgent";
    }

    @Override
    public String getRoleDetails() {
        return "Rental Agent serving " + getBranchId();
    }
}
