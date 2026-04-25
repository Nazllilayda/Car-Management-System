package carmanagement;

public class BranchManager extends Employee {
    public BranchManager(String employeeId, String fullName, String branchId) {
        super(employeeId, fullName, branchId);
    }

    @Override
    public String getRole() {
        return "BranchManager";
    }

    @Override
    public String getRoleDetails() {
        return "Branch Manager at " + getBranchId();
    }
}
