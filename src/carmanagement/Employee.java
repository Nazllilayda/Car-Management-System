package carmanagement;

// Abstraction: every employee shares identity data but exposes role-specific behavior.
public abstract class Employee {
    private final String employeeId;
    private final String fullName;
    private final String branchId;

    protected Employee(String employeeId, String fullName, String branchId) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.branchId = branchId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBranchId() {
        return branchId;
    }

    public abstract String getRole();

    public abstract String getRoleDetails();

    public String toFileRecord() {
        return String.join("|", getRole(), employeeId, fullName, branchId);
    }

    @Override
    public String toString() {
        return employeeId + " - " + fullName + " (" + getRoleDetails() + ")";
    }
}
