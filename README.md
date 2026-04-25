# Velocity Harbor Solutions - FleetOrbit Rental Suite

FleetOrbit Rental Suite is a Java Swing car rental agency management system designed for a multi-branch business model. The application supports vehicle inventory management, customer loyalty tiers, reservation workflows, invoice generation, maintenance scheduling, and damage assessment with `.txt` file persistence.

## Project Highlights

- Company name: `Velocity Harbor Solutions`
- Product name: `FleetOrbit Rental Suite`
- Language: `Java`
- GUI: `Java Swing`
- Persistence: plain `.txt` files inside `/Users/ilayda/Documents/GitHub/CarManagementSystem/data`

## Required Features Covered

- Vehicle inventory by type and branch
- Reservation creation and management
- Invoice generation with daily rate, insurance, mileage, and damage calculations
- Loyalty tiers with `Bronze`, `Silver`, and `Gold` discounts

## Bonus Features Covered

- Vehicle maintenance scheduling
- Mechanic assignment
- Damage assessment workflow during return

## OOP Concept Mapping

1. Encapsulation: private fields with controlled access across domain classes such as `Customer`, `Reservation`, and `Invoice`.
2. Inheritance: `EconomyVehicle`, `SUVVehicle`, `LuxuryVehicle`, and `VanVehicle` extend `Vehicle`; employee roles extend `Employee`.
3. Polymorphism: vehicle-specific `getDailyRate`, `getMileagePolicy`, and rental calculations run through `Vehicle` references.
4. Abstraction: `Vehicle` and `Employee` are abstract base classes.
5. Composition: `Reservation` is composed of `Customer`, `Vehicle`, `Branch`, and `RentalAgent`.
6. Interfaces / Abstract Classes: abstract classes are used for shared contracts and behavior.
7. Method Overriding: vehicle and employee subclasses override inherited methods.
8. Method Overloading: overloaded constructors in `Customer`, overloaded `registerCustomer`, and overloaded `generateInvoice`.
9. Exception Handling with Custom Exceptions: `VehicleNotAvailableException`, `InvalidReservationException`, and `BranchNotFoundException`.

## Folder Structure

```text
CarManagementSystem/
├── data/
├── docs/
│   └── UML.md
├── out/
└── src/
    └── carmanagement/
```

## Compile

```bash
cd /Users/ilayda/Documents/GitHub/CarManagementSystem
javac -d out src/carmanagement/*.java
```

## Run

```bash
cd /Users/ilayda/Documents/GitHub/CarManagementSystem
java -cp out carmanagement.Main
```

## NetBeans Notes

1. Open NetBeans.
2. Create a `Java with Ant > Java Application` project named `CarManagementSystem`.
3. Copy the files in `/Users/ilayda/Documents/GitHub/CarManagementSystem/src/carmanagement` into the NetBeans `Source Packages > carmanagement` package.
4. Run `Main.java`.

## VS Code Notes

1. Open `/Users/ilayda/Documents/GitHub/CarManagementSystem` in VS Code.
2. Install the `Extension Pack for Java` if VS Code asks for it.
3. Open [Main.java](/Users/ilayda/Documents/GitHub/CarManagementSystem/src/carmanagement/Main.java:1).
4. Press `Run` above the `main` method, or use the `Run FleetOrbit Rental Suite` launch configuration.
5. You can also use the included VS Code tasks to compile and run the project.

## Presentation Notes

- The GUI title bar already displays the company and product names.
- The application demonstrates branch-aware inventory checks and reservation conflict prevention.
- The `docs/UML.md` file contains UML-style diagrams for use case, activity, sequence, and class views.
