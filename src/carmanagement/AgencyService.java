package carmanagement;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgencyService {
    public static final String COMPANY_NAME = "Velocity Harbor Solutions";
    public static final String PRODUCT_NAME = "FleetOrbit Rental Suite";

    private final TextStorageService storageService;
    private final Map<String, Branch> branches = new LinkedHashMap<>();
    private final Map<String, Vehicle> vehicles = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Employee> employees = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();
    private final Map<String, Invoice> invoices = new LinkedHashMap<>();
    private final Map<String, MaintenanceRecord> maintenanceRecords = new LinkedHashMap<>();
    private final Map<String, DamageReport> damageReports = new LinkedHashMap<>();

    public AgencyService(Path dataDirectory) {
        this.storageService = new TextStorageService(dataDirectory);
    }

    public void initialize() throws IOException {
        loadData();
        if (branches.isEmpty()) {
            seedSampleData();
        }
        ensureDemoCatalog();
        persistAll();
    }

    public Collection<Branch> getBranches() {
        return branches.values();
    }

    public Collection<Vehicle> getVehicles() {
        return vehicles.values();
    }

    public Collection<Customer> getCustomers() {
        return customers.values();
    }

    public Collection<Employee> getEmployees() {
        return employees.values();
    }

    public Collection<Reservation> getReservations() {
        return reservations.values();
    }

    public Collection<Invoice> getInvoices() {
        return invoices.values();
    }

    public Collection<MaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecords.values();
    }

    public Collection<DamageReport> getDamageReports() {
        return damageReports.values();
    }

    public List<Vehicle> searchAvailableVehicles(String branchId, String vehicleType, LocalDate startDate, LocalDate endDate)
            throws BranchNotFoundException {
        requireBranch(branchId);
        return vehicles.values().stream()
                .filter(vehicle -> vehicle.getBranchId().equalsIgnoreCase(branchId))
                .filter(vehicle -> vehicleType.equals("All") || vehicle.getVehicleType().equalsIgnoreCase(vehicleType))
                .filter(vehicle -> vehicle.getStatus() == VehicleStatus.AVAILABLE || vehicle.getStatus() == VehicleStatus.RESERVED)
                .filter(vehicle -> !hasReservationConflict(vehicle.getVehicleId(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public Customer registerCustomer(String customerId, String fullName, String phoneNumber, String licenseNumber) throws IOException {
        Customer customer = new Customer(customerId, fullName, phoneNumber, licenseNumber);
        return registerCustomer(customer);
    }

    // Method overloading: both GUI-driven and object-driven registration are supported.
    public Customer registerCustomer(Customer customer) throws IOException {
        customers.put(customer.getCustomerId(), customer);
        persistCustomers();
        return customer;
    }

    public Reservation createReservation(String reservationId, String customerId, String vehicleId,
            String pickupBranchId, String returnBranchId, InsuranceOption insuranceOption,
            String agentId, LocalDate startDate, LocalDate endDate)
            throws InvalidReservationException, VehicleNotAvailableException, BranchNotFoundException, IOException {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new InvalidReservationException("End date must be after start date.");
        }

        Customer customer = customers.get(customerId);
        Vehicle vehicle = vehicles.get(vehicleId);
        Branch pickupBranch = requireBranch(pickupBranchId);
        Branch returnBranch = requireBranch(returnBranchId);
        Employee employee = employees.get(agentId);

        if (customer == null) {
            throw new InvalidReservationException("Customer not found.");
        }
        if (vehicle == null) {
            throw new InvalidReservationException("Vehicle not found.");
        }
        if (!(employee instanceof RentalAgent rentalAgent)) {
            throw new InvalidReservationException("Selected employee is not a rental agent.");
        }
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new VehicleNotAvailableException("Vehicle is currently under maintenance.");
        }
        if (hasReservationConflict(vehicleId, startDate, endDate)) {
            throw new VehicleNotAvailableException("Vehicle has a conflicting reservation.");
        }

        Reservation reservation = new Reservation(reservationId, customer, vehicle, pickupBranch, returnBranch,
                insuranceOption, rentalAgent, startDate, endDate, ReservationStatus.RESERVED);
        reservations.put(reservationId, reservation);
        vehicle.setStatus(VehicleStatus.RESERVED);
        persistVehicles();
        persistReservations();
        return reservation;
    }

    public Reservation pickUpVehicle(String reservationId) throws InvalidReservationException, IOException {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found.");
        }
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new InvalidReservationException("Only reserved vehicles can be picked up.");
        }

        reservation.setStatus(ReservationStatus.PICKED_UP);
        reservation.getVehicle().setStatus(VehicleStatus.RENTED);
        persistVehicles();
        persistReservations();
        return reservation;
    }

    public Invoice returnVehicle(String reservationId, int mileageDriven, String damageNotes)
            throws InvalidReservationException, IOException {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new InvalidReservationException("Reservation not found.");
        }
        if (reservation.getStatus() != ReservationStatus.PICKED_UP) {
            throw new InvalidReservationException("Vehicle must be picked up before return.");
        }

        Vehicle vehicle = reservation.getVehicle();
        double baseAmount = vehicle.calculateBaseRental(reservation.getStartDate(), reservation.getEndDate(),
                reservation.getInsuranceOption());
        double discountAmount = baseAmount * reservation.getCustomer().getDiscountRate();
        double damageFee = damageNotes == null || damageNotes.isBlank() ? 0.0 : 175.0;
        double mileageFee = calculateMileageFee(vehicle, mileageDriven);

        Invoice invoice = generateInvoice(reservationId, baseAmount, discountAmount, damageFee, mileageFee);
        invoices.put(invoice.getInvoiceId(), invoice);

        if (damageFee > 0) {
            DamageReport damageReport = new DamageReport(nextId("DR", damageReports.size() + 1), reservationId, damageNotes, damageFee);
            damageReports.put(damageReport.getReportId(), damageReport);
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        reservation.getCustomer().addLoyaltyPoints((int) Math.round(invoice.getTotalAmount() / 10.0));

        persistAll();
        return invoice;
    }

    public MaintenanceRecord scheduleMaintenance(String vehicleId, String mechanicId, LocalDate scheduledDate, String description)
            throws InvalidReservationException, IOException {
        Vehicle vehicle = vehicles.get(vehicleId);
        Employee mechanic = employees.get(mechanicId);
        if (vehicle == null) {
            throw new InvalidReservationException("Vehicle not found for maintenance.");
        }
        if (!(mechanic instanceof Mechanic)) {
            throw new InvalidReservationException("Selected employee is not a mechanic.");
        }

        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        MaintenanceRecord record = new MaintenanceRecord(nextId("MT", maintenanceRecords.size() + 1),
                vehicleId, mechanicId, scheduledDate, description);
        maintenanceRecords.put(record.getMaintenanceId(), record);
        persistVehicles();
        persistMaintenance();
        return record;
    }

    public Branch requireBranch(String branchId) throws BranchNotFoundException {
        Branch branch = branches.get(branchId);
        if (branch == null) {
            throw new BranchNotFoundException("Branch not found: " + branchId);
        }
        return branch;
    }

    // Method overloading: one invoice method with explicit fees, one fallback for clean returns.
    public Invoice generateInvoice(String reservationId, double baseAmount, double discountAmount,
            double damageFee, double mileageFee) {
        double totalAmount = baseAmount - discountAmount + damageFee + mileageFee;
        return new Invoice(nextId("INV", invoices.size() + 1), reservationId, baseAmount, discountAmount, damageFee, mileageFee, totalAmount);
    }

    public Invoice generateInvoice(String reservationId, double baseAmount, double discountAmount) {
        return generateInvoice(reservationId, baseAmount, discountAmount, 0.0, 0.0);
    }

    public List<RentalAgent> getRentalAgents() {
        return employees.values().stream()
                .filter(employee -> employee instanceof RentalAgent)
                .map(employee -> (RentalAgent) employee)
                .collect(Collectors.toList());
    }

    public List<Mechanic> getMechanics() {
        return employees.values().stream()
                .filter(employee -> employee instanceof Mechanic)
                .map(employee -> (Mechanic) employee)
                .collect(Collectors.toList());
    }

    public String buildDashboardSummary() {
        return "Company: " + COMPANY_NAME + "\n"
                + "Product: " + PRODUCT_NAME + "\n"
                + "Branches: " + branches.size() + "\n"
                + "Vehicles: " + vehicles.size() + "\n"
                + "Customers: " + customers.size() + "\n"
                + "Reservations: " + reservations.size() + "\n"
                + "Invoices: " + invoices.size() + "\n"
                + "Maintenance Records: " + maintenanceRecords.size() + "\n"
                + "Damage Reports: " + damageReports.size();
    }

    private double calculateMileageFee(Vehicle vehicle, int mileageDriven) {
        int extraMileage = Math.max(0, mileageDriven - vehicle.getMileagePolicy());
        return extraMileage * 0.75;
    }

    private boolean hasReservationConflict(String vehicleId, LocalDate startDate, LocalDate endDate) {
        for (Reservation reservation : reservations.values()) {
            if (!reservation.getVehicle().getVehicleId().equalsIgnoreCase(vehicleId)) {
                continue;
            }
            if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
                continue;
            }
            boolean overlaps = !endDate.isBefore(reservation.getStartDate()) && !startDate.isAfter(reservation.getEndDate());
            if (overlaps) {
                return true;
            }
        }
        return false;
    }

    private void loadData() throws IOException {
        loadBranches();
        loadCustomers();
        loadEmployees();
        loadVehicles();
        loadReservations();
        loadInvoices();
        loadMaintenance();
        loadDamageReports();
    }

    private void seedSampleData() {
        branches.put("BR01", new Branch("BR01", "Downtown Hub", "Istanbul"));
        branches.put("BR02", new Branch("BR02", "Airport Hub", "Ankara"));
        branches.put("BR03", new Branch("BR03", "Coastal Hub", "Izmir"));
        ensureDemoCatalog();
    }

    private void ensureDemoCatalog() {
        branches.putIfAbsent("BR01", new Branch("BR01", "Downtown Hub", "Istanbul"));
        branches.putIfAbsent("BR02", new Branch("BR02", "Airport Hub", "Ankara"));
        branches.putIfAbsent("BR03", new Branch("BR03", "Coastal Hub", "Izmir"));

        employees.putIfAbsent("EMP01", new BranchManager("EMP01", "Lena Brooks", "BR01"));
        employees.putIfAbsent("EMP02", new RentalAgent("EMP02", "David Cole", "BR01"));
        employees.putIfAbsent("EMP03", new Mechanic("EMP03", "Mia Patel", "BR01"));
        employees.putIfAbsent("EMP04", new RentalAgent("EMP04", "Noah Reed", "BR02"));
        employees.putIfAbsent("EMP05", new Mechanic("EMP05", "Ava Turner", "BR02"));
        employees.putIfAbsent("EMP06", new BranchManager("EMP06", "Sophia Bennett", "BR03"));
        employees.putIfAbsent("EMP07", new RentalAgent("EMP07", "Ethan Walker", "BR03"));
        employees.putIfAbsent("EMP08", new Mechanic("EMP08", "Chloe Rivera", "BR03"));

        customers.putIfAbsent("CUS01", new Customer("CUS01", "Emma Carter", "555-1001", "LIC1001", 240));
        customers.putIfAbsent("CUS02", new Customer("CUS02", "Lucas Nguyen", "555-1002", "LIC1002", 540));
        customers.putIfAbsent("CUS03", new Customer("CUS03", "Olivia Stone", "555-1003", "LIC1003", 120));
        customers.putIfAbsent("CUS04", new Customer("CUS04", "Daniel Kim", "555-1004", "LIC1004", 30));

        vehicles.putIfAbsent("VH01", new EconomyVehicle("VH01", "34ECO101", "Toyota", "Yaris", "White", 2023, "BR01"));
        vehicles.putIfAbsent("VH02", new SUVVehicle("VH02", "34SUV202", "Nissan", "X-Trail", "Black", 2022, "BR01"));
        vehicles.putIfAbsent("VH03", new LuxuryVehicle("VH03", "06LUX303", "BMW", "5 Series", "Midnight Blue", 2024, "BR02"));
        vehicles.putIfAbsent("VH04", new VanVehicle("VH04", "06VAN404", "Ford", "Transit", "Silver", 2021, "BR02"));
        vehicles.putIfAbsent("VH05", new EconomyVehicle("VH05", "34ECO505", "Honda", "City", "Red", 2024, "BR01"));
        vehicles.putIfAbsent("VH06", new EconomyVehicle("VH06", "35ECO606", "Hyundai", "i20", "Gray", 2023, "BR03"));
        vehicles.putIfAbsent("VH07", new EconomyVehicle("VH07", "06ECO707", "Renault", "Clio", "Blue", 2022, "BR02"));
        vehicles.putIfAbsent("VH08", new SUVVehicle("VH08", "35SUV808", "Kia", "Sportage", "Pearl White", 2024, "BR03"));
        vehicles.putIfAbsent("VH09", new SUVVehicle("VH09", "34SUV909", "Peugeot", "3008", "Graphite", 2023, "BR01"));
        vehicles.putIfAbsent("VH10", new SUVVehicle("VH10", "06SUV110", "Volkswagen", "Tiguan", "Silver", 2022, "BR02"));
        vehicles.putIfAbsent("VH11", new LuxuryVehicle("VH11", "35LUX211", "Mercedes-Benz", "E-Class", "Obsidian Black", 2024, "BR03"));
        vehicles.putIfAbsent("VH12", new LuxuryVehicle("VH12", "34LUX312", "Audi", "A6", "Glacier White", 2023, "BR01"));
        vehicles.putIfAbsent("VH13", new LuxuryVehicle("VH13", "06LUX413", "Volvo", "S90", "Champagne", 2024, "BR02"));
        vehicles.putIfAbsent("VH14", new VanVehicle("VH14", "35VAN514", "Mercedes-Benz", "Vito", "Navy", 2022, "BR03"));
        vehicles.putIfAbsent("VH15", new VanVehicle("VH15", "34VAN615", "Volkswagen", "Caravelle", "White", 2023, "BR01"));
        vehicles.putIfAbsent("VH16", new VanVehicle("VH16", "06VAN716", "Peugeot", "Traveller", "Bronze", 2024, "BR02"));
        vehicles.putIfAbsent("VH17", new EconomyVehicle("VH17", "34MIC117", "Nissan", "Micra 1.0 Vision", "Green", 2024, "BR01"));
    }

    private void loadBranches() throws IOException {
        for (String line : storageService.readRecords("branches.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length == 3) {
                branches.put(parts[0], new Branch(parts[0], parts[1], parts[2]));
            }
        }
    }

    private void loadCustomers() throws IOException {
        for (String line : storageService.readRecords("customers.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length == 5) {
                customers.put(parts[0], new Customer(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4])));
            }
        }
    }

    private void loadEmployees() throws IOException {
        for (String line : storageService.readRecords("employees.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length != 4) {
                continue;
            }
            Employee employee;
            switch (parts[0]) {
                case "BranchManager" -> employee = new BranchManager(parts[1], parts[2], parts[3]);
                case "RentalAgent" -> employee = new RentalAgent(parts[1], parts[2], parts[3]);
                case "Mechanic" -> employee = new Mechanic(parts[1], parts[2], parts[3]);
                default -> employee = null;
            }
            if (employee != null) {
                employees.put(employee.getEmployeeId(), employee);
            }
        }
    }

    private void loadVehicles() throws IOException {
        for (String line : storageService.readRecords("vehicles.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length != 8 && parts.length != 9) {
                continue;
            }
            String color = parts.length == 9 ? parts[5] : "Unspecified";
            int year = Integer.parseInt(parts.length == 9 ? parts[6] : parts[5]);
            String branchId = parts.length == 9 ? parts[7] : parts[6];
            String status = parts.length == 9 ? parts[8] : parts[7];
            Vehicle vehicle = createVehicle(parts[0], parts[1], parts[2], parts[3], parts[4], color, year, branchId);
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.valueOf(status));
                vehicles.put(vehicle.getVehicleId(), vehicle);
            }
        }
    }

    private void loadReservations() throws IOException {
        for (String line : storageService.readRecords("reservations.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length != 10) {
                continue;
            }
            Customer customer = customers.get(parts[1]);
            Vehicle vehicle = vehicles.get(parts[2]);
            Branch pickupBranch = branches.get(parts[3]);
            Branch returnBranch = branches.get(parts[4]);
            Employee employee = employees.get(parts[6]);
            if (customer == null || vehicle == null || pickupBranch == null || returnBranch == null || !(employee instanceof RentalAgent agent)) {
                continue;
            }
            Reservation reservation = new Reservation(parts[0], customer, vehicle, pickupBranch, returnBranch,
                    InsuranceOption.valueOf(parts[5]), agent, LocalDate.parse(parts[7]), LocalDate.parse(parts[8]),
                    ReservationStatus.valueOf(parts[9]));
            reservations.put(reservation.getReservationId(), reservation);
        }
    }

    private void loadInvoices() throws IOException {
        for (String line : storageService.readRecords("invoices.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length == 7) {
                invoices.put(parts[0], new Invoice(parts[0], parts[1],
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Double.parseDouble(parts[6])));
            }
        }
    }

    private void loadMaintenance() throws IOException {
        for (String line : storageService.readRecords("maintenance.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length == 5) {
                maintenanceRecords.put(parts[0],
                        new MaintenanceRecord(parts[0], parts[1], parts[2], LocalDate.parse(parts[3]), parts[4]));
            }
        }
    }

    private void loadDamageReports() throws IOException {
        for (String line : storageService.readRecords("damage_reports.txt")) {
            String[] parts = line.split("\\|");
            if (parts.length == 4) {
                damageReports.put(parts[0], new DamageReport(parts[0], parts[1], parts[2], Double.parseDouble(parts[3])));
            }
        }
    }

    private Vehicle createVehicle(String vehicleType, String vehicleId, String plateNumber, String brand, String model,
            String color, int year, String branchId) {
        return switch (vehicleType) {
            case "Economy" -> new EconomyVehicle(vehicleId, plateNumber, brand, model, color, year, branchId);
            case "SUV" -> new SUVVehicle(vehicleId, plateNumber, brand, model, color, year, branchId);
            case "Luxury" -> new LuxuryVehicle(vehicleId, plateNumber, brand, model, color, year, branchId);
            case "Van" -> new VanVehicle(vehicleId, plateNumber, brand, model, color, year, branchId);
            default -> null;
        };
    }

    public void persistAll() throws IOException {
        persistBranches();
        persistCustomers();
        persistEmployees();
        persistVehicles();
        persistReservations();
        persistInvoices();
        persistMaintenance();
        persistDamageReports();
    }

    private void persistBranches() throws IOException {
        List<String> records = branches.values().stream()
                .map(branch -> String.join("|", branch.getBranchId(), branch.getName(), branch.getCity()))
                .collect(Collectors.toList());
        storageService.writeRecords("branches.txt", records);
    }

    private void persistCustomers() throws IOException {
        storageService.writeRecords("customers.txt", customers.values().stream()
                .map(Customer::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistEmployees() throws IOException {
        storageService.writeRecords("employees.txt", employees.values().stream()
                .map(Employee::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistVehicles() throws IOException {
        storageService.writeRecords("vehicles.txt", vehicles.values().stream()
                .map(Vehicle::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistReservations() throws IOException {
        storageService.writeRecords("reservations.txt", reservations.values().stream()
                .map(Reservation::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistInvoices() throws IOException {
        storageService.writeRecords("invoices.txt", invoices.values().stream()
                .map(Invoice::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistMaintenance() throws IOException {
        storageService.writeRecords("maintenance.txt", maintenanceRecords.values().stream()
                .map(MaintenanceRecord::toFileRecord)
                .collect(Collectors.toList()));
    }

    private void persistDamageReports() throws IOException {
        storageService.writeRecords("damage_reports.txt", damageReports.values().stream()
                .map(DamageReport::toFileRecord)
                .collect(Collectors.toList()));
    }

    public String nextCustomerId() {
        return nextId("CUS", customers.size() + 1);
    }

    public String nextReservationId() {
        return nextId("RES", reservations.size() + 1);
    }

    private String nextId(String prefix, int count) {
        return prefix + String.format("%03d", count);
    }

    public List<String> buildReservationLines() {
        return reservations.values().stream()
                .map(Reservation::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> buildInvoiceLines() {
        return invoices.values().stream()
                .map(Invoice::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> buildVehicleLines() {
        return vehicles.values().stream()
                .map(Vehicle::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
