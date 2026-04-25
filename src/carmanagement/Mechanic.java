package carmanagement;

public class Mechanic extends Employee {
    public Mechanic(String employeeId, String fullName, String branchId) {
        super(employeeId, fullName, branchId);
    }

    @Override
    public String getRole() {
        return "Mechanic";
    }

    @Override
    public String getRoleDetails() {
        return "Mechanic assigned to " + getBranchId();
    }
}
