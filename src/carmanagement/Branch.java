package carmanagement;

public class Branch {
    // Encapsulation: branch state is controlled through getters only.
    private final String branchId;
    private final String name;
    private final String city;

    public Branch(String branchId, String name, String city) {
        this.branchId = branchId;
        this.name = name;
        this.city = city;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return branchId + " - " + name + " (" + city + ")";
    }
}
