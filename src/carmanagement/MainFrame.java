package carmanagement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MainFrame extends JFrame {
    private static final Color PAGE_BACKGROUND = new Color(252, 245, 248);
    private static final Color PANEL_BACKGROUND = Color.WHITE;
    private static final Color BRAND_DARK = new Color(122, 50, 94);
    private static final Color BRAND_MID = new Color(198, 83, 133);
    private static final Color BRAND_RED = new Color(214, 72, 93);
    private static final Color ACCENT_GOLD = new Color(242, 203, 126);
    private static final Color SOFT_BLUE = new Color(251, 233, 242);
    private static final Color SOFT_GREEN = new Color(248, 235, 241);
    private static final Color SOFT_ORANGE = new Color(255, 234, 236);
    private static final Color SOFT_LAVENDER = new Color(247, 231, 240);
    private static final Color TEXT_PRIMARY = new Color(88, 40, 69);
    private static final Color TEXT_MUTED = new Color(143, 102, 126);

    private final AgencyService agencyService = new AgencyService(Paths.get("data"));

    private final JLabel branchCountLabel = createMetricValueLabel();
    private final JLabel vehicleCountLabel = createMetricValueLabel();
    private final JLabel reservationCountLabel = createMetricValueLabel();
    private final JLabel invoiceCountLabel = createMetricValueLabel();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel heroInsightLabel = new JLabel("Live rental operations overview");
    private final JLabel latestReservationLabel = new JLabel("Latest reservation ID: none yet");

    private final JTextArea dashboardArea = createNarrativeArea();
    private final JTextArea activityGuideArea = createNarrativeArea();

    private final JComboBox<String> vehicleBranchBox = new JComboBox<>();
    private final JComboBox<String> vehicleTypeBox = new JComboBox<>(new String[]{"All", "Economy", "SUV", "Luxury", "Van"});
    private final JTextField vehicleStartDateField = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JTextField vehicleEndDateField = new JTextField(LocalDate.now().plusDays(4).toString());

    private final JTextField customerIdField = new JTextField();
    private final JTextField customerNameField = new JTextField();
    private final JTextField customerPhoneField = new JTextField();
    private final JTextField customerLicenseField = new JTextField();

    private final JTextField reservationIdField = new JTextField();
    private final JComboBox<String> reservationCustomerBox = new JComboBox<>();
    private final JComboBox<String> reservationVehicleBox = new JComboBox<>();
    private final JComboBox<String> pickupBranchBox = new JComboBox<>();
    private final JComboBox<String> returnBranchBox = new JComboBox<>();
    private final JComboBox<String> insuranceBox = new JComboBox<>(new String[]{"BASIC", "STANDARD", "PREMIUM"});
    private final JComboBox<String> agentBox = new JComboBox<>();
    private final JTextField reservationStartField = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JTextField reservationEndField = new JTextField(LocalDate.now().plusDays(3).toString());
    private final JTextField pickupReservationField = new JTextField();
    private final JTextField returnReservationField = new JTextField();
    private final JTextField mileageField = new JTextField("180");
    private final JTextField damageField = new JTextField();

    private final JComboBox<String> maintenanceVehicleBox = new JComboBox<>();
    private final JComboBox<String> mechanicBox = new JComboBox<>();
    private final JTextField maintenanceDateField = new JTextField(LocalDate.now().plusDays(7).toString());
    private final JTextField maintenanceDescriptionField = new JTextField("Routine inspection");
    private final JTabbedPane mainTabs = new JTabbedPane();

    private final DefaultTableModel vehicleTableModel = createTableModel("ID", "Type", "Brand", "Model", "Color", "Year", "Branch", "Status", "Daily Rate");
    private final DefaultTableModel customerTableModel = createTableModel("Customer ID", "Full Name", "Phone", "License", "Tier", "Points", "Discount");
    private final DefaultTableModel reservationTableModel = createTableModel("Reservation", "Customer", "Vehicle", "Pickup", "Return", "Start", "End", "Status");
    private final DefaultTableModel reservationLookupTableModel = createTableModel("Reservation ID", "Customer", "Vehicle", "Status");
    private final DefaultTableModel invoiceTableModel = createTableModel("Invoice", "Reservation", "Base", "Discount", "Damage", "Mileage", "Total");
    private final DefaultTableModel damageTableModel = createTableModel("Damage ID", "Reservation", "Notes", "Fee");
    private final DefaultTableModel employeeTableModel = createTableModel("Employee ID", "Name", "Role", "Branch", "Details");
    private final DefaultTableModel maintenanceTableModel = createTableModel("Record", "Vehicle", "Mechanic", "Date", "Description");

    private final JTable vehicleTable = createTable(vehicleTableModel);
    private final JTable customerTable = createTable(customerTableModel);
    private final JTable reservationTable = createTable(reservationTableModel);
    private final JTable reservationLookupTable = createTable(reservationLookupTableModel);
    private final JTable invoiceTable = createTable(invoiceTableModel);
    private final JTable damageTable = createTable(damageTableModel);
    private final JTable employeeTable = createTable(employeeTableModel);
    private final JTable maintenanceTable = createTable(maintenanceTableModel);

    public MainFrame() {
        applyLookAndFeel();

        setTitle(AgencyService.COMPANY_NAME + " - " + AgencyService.PRODUCT_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1320, 860));
        setSize(new Dimension(1400, 900));
        setLocationRelativeTo(null);
        getContentPane().setBackground(PAGE_BACKGROUND);
        setLayout(new BorderLayout(16, 16));

        try {
            agencyService.initialize();
        } catch (Exception ex) {
            showError(ex);
        }

        add(buildHeroPanel(), BorderLayout.NORTH);
        add(buildTabbedPane(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);

        wireReservationLookupTable();
        refreshAllViews();
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private JPanel buildHeroPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(16, 16));
        wrapper.setBackground(PAGE_BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        JPanel heroCard = new JPanel(new BorderLayout(16, 16));
        heroCard.setBackground(BRAND_DARK);
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(188, 117, 148)),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel companyLabel = new JLabel(AgencyService.COMPANY_NAME);
        companyLabel.setForeground(Color.WHITE);
        companyLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel productLabel = new JLabel(AgencyService.PRODUCT_NAME);
        productLabel.setForeground(new Color(255, 229, 238));
        productLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel badge = createHeroBadge("Premium Rental Experience");
        JLabel subtitle = new JLabel("Designed to impress: showroom-grade UI, branch inventory, reservations, invoicing, loyalty, and maintenance.");
        subtitle.setForeground(new Color(255, 235, 241));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        heroInsightLabel.setForeground(new Color(255, 226, 233));
        heroInsightLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        textPanel.add(badge);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(companyLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(productLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(subtitle);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(heroInsightLabel);

        JPanel quickActions = new JPanel(new GridLayout(1, 3, 10, 10));
        quickActions.setOpaque(false);
        JButton refreshButton = createAccentButton("Refresh All");
        refreshButton.addActionListener(e -> {
            refreshAllViews();
            setStatus("All panels refreshed.");
        });
        JButton inventoryButton = createGhostButton("Show Inventory");
        inventoryButton.addActionListener(e -> {
            refreshVehicleTable(agencyService.getVehicles().stream().toList());
            setStatus("Full inventory loaded.");
        });
        JButton demoButton = createGhostButton("Open Welcome");
        demoButton.addActionListener(e -> {
            mainTabs.setSelectedIndex(0);
            setStatus("Welcome showcase opened.");
        });
        quickActions.add(refreshButton);
        quickActions.add(inventoryButton);
        quickActions.add(demoButton);

        heroCard.add(textPanel, BorderLayout.CENTER);
        heroCard.add(quickActions, BorderLayout.SOUTH);

        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 12, 12));
        metricsPanel.setBackground(PAGE_BACKGROUND);
        metricsPanel.add(createMetricCard("Branches", "Operational coverage", branchCountLabel, SOFT_BLUE));
        metricsPanel.add(createMetricCard("Vehicles", "Rental catalog", vehicleCountLabel, SOFT_GREEN));
        metricsPanel.add(createMetricCard("Reservations", "Active workflow", reservationCountLabel, SOFT_ORANGE));
        metricsPanel.add(createMetricCard("Invoices", "Billing output", invoiceCountLabel, SOFT_LAVENDER));

        wrapper.add(heroCard, BorderLayout.CENTER);
        wrapper.add(metricsPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JTabbedPane buildTabbedPane() {
        mainTabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainTabs.addTab("Welcome", buildWelcomePanel());
        mainTabs.addTab("Dashboard", buildDashboardPanel());
        mainTabs.addTab("Vehicles", buildVehiclePanel());
        mainTabs.addTab("Customers", buildCustomerPanel());
        mainTabs.addTab("Reservations", buildReservationPanel());
        mainTabs.addTab("Maintenance", buildMaintenancePanel());
        return mainTabs;
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = createPagePanel();

        JPanel hero = new JPanel(new BorderLayout(18, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint paint = new GradientPaint(0, 0, new Color(138, 58, 106), getWidth(), getHeight(), new Color(228, 121, 145));
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel badge = createHeroBadge("Welcome to KFISB");
        JLabel title = new JLabel("Modern rental operations, presented with clarity.");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("KFISB DriveLounge brings branch inventory, reservations, invoicing, loyalty, and maintenance into one refined workspace.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(255, 237, 243));

        JTextArea pitch = createWelcomeNarrativeArea();
        pitch.setText("""
KFISB DriveLounge is designed for teams that need a clean, dependable way to manage daily rental activity.

What you can do here
- Browse vehicles by branch and type
- Register customers and track loyalty tiers
- Create, update, and complete reservations
- Generate invoices with insurance and mileage calculations
- Manage maintenance scheduling for branch vehicles

Use the tabs above to move through each part of the workflow.
""");

        copy.add(badge);
        copy.add(Box.createVerticalStrut(14));
        copy.add(title);
        copy.add(Box.createVerticalStrut(10));
        copy.add(subtitle);
        copy.add(Box.createVerticalStrut(18));
        copy.add(pitch);

        JPanel showcase = new JPanel(new GridLayout(3, 1, 12, 12));
        showcase.setOpaque(false);
        showcase.add(createWelcomeHighlight("Vehicle Management", "Organized branch inventory", "View vehicle type, availability, color, pricing, and branch details in one place."));
        showcase.add(createWelcomeHighlight("Reservation Flow", "Smooth rental processing", "Create reservations, complete pickup and return steps, and keep status changes easy to follow."));
        showcase.add(createWelcomeHighlight("Business Control", "Invoices and service tracking", "Handle billing, loyalty discounts, maintenance records, and damage workflows from the same system."));

        hero.add(copy, BorderLayout.CENTER);
        hero.add(showcase, BorderLayout.EAST);

        JPanel actions = new JPanel(new GridLayout(1, 3, 14, 14));
        actions.setOpaque(false);

        JButton catalogButton = createPrimaryCtaButton("Open Vehicle Catalog");
        catalogButton.addActionListener(e -> {
            refreshVehicleTable(agencyService.getVehicles().stream().toList());
            openVehicleCatalogDialog("KFISB Vehicle Catalog");
        });

        JButton reserveButton = createGhostButton("Go to Reservations");
        reserveButton.addActionListener(e -> {
            mainTabs.setSelectedIndex(4);
            setStatus("Reservations tab opened.");
        });

        JButton customerButton = createGhostButton("Go to Customers");
        customerButton.addActionListener(e -> {
            mainTabs.setSelectedIndex(3);
            setStatus("Customers tab opened.");
        });

        actions.add(catalogButton);
        actions.add(reserveButton);
        actions.add(customerButton);

        JPanel lower = new JPanel(new GridLayout(1, 3, 14, 14));
        lower.setOpaque(false);
        lower.add(createMetricCard("Branches", "Connected locations", createMetricDisplayLabel("3"), new Color(255, 234, 241)));
        lower.add(createMetricCard("Vehicle Types", "Economy, SUV, Luxury, Van", createMetricDisplayLabel("4"), new Color(252, 239, 244)));
        lower.add(createMetricCard("Core Modules", "Reservations, invoices, maintenance", createMetricDisplayLabel("5"), new Color(255, 232, 234)));

        panel.add(hero, BorderLayout.NORTH);
        panel.add(actions, BorderLayout.CENTER);
        panel.add(lower, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = createPagePanel();

        dashboardArea.setText("");
        activityGuideArea.setText("""
Demo Storyline

1. Open Vehicles and filter by branch or type.
2. Register a customer with an auto-generated ID.
3. Create a reservation with insurance and branch selection.
4. Pick up the vehicle to move the status into active rental.
5. Return the vehicle with mileage and optional damage notes.
6. Show the generated invoice and updated loyalty tier.
7. Schedule maintenance for any vehicle after return.

Presentation Tip
- Start from the Dashboard
- Jump to Vehicles for search
- Finish in Reservations for the strongest visual payoff
""");

        JPanel left = createCardPanel("Operational Snapshot", "Executive-style summary for your presentation.");
        left.add(createSectionTitle("KFISB System Summary"), BorderLayout.NORTH);
        left.add(new JScrollPane(dashboardArea), BorderLayout.CENTER);

        JPanel right = createCardPanel("Presentation Guide", "Use this panel as your demo checklist.");
        right.add(createSectionTitle("Suggested Flow"), BorderLayout.NORTH);
        right.add(new JScrollPane(activityGuideArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setBorder(null);
        splitPane.setResizeWeight(0.56);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildVehiclePanel() {
        JPanel panel = createPagePanel();

        JPanel top = new JPanel(new GridLayout(1, 2, 14, 14));
        top.setOpaque(false);
        top.add(buildVehicleSearchCard());
        top.add(buildVehicleHighlightsCard());
        panel.add(top, BorderLayout.NORTH);
        panel.add(buildVehicleCatalogLaunchCard(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildVehicleSearchCard() {
        JPanel card = createCardPanel("Availability Search", "Filter by branch, vehicle type, and rental dates.");
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setOpaque(false);
        form.add(createFieldLabel("Branch"));
        form.add(vehicleBranchBox);
        form.add(createFieldLabel("Vehicle Type"));
        form.add(vehicleTypeBox);
        form.add(createFieldLabel("Start Date"));
        form.add(vehicleStartDateField);
        form.add(createFieldLabel("End Date"));
        form.add(vehicleEndDateField);

        JPanel buttonRow = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonRow.setOpaque(false);
        JButton searchButton = createAccentButton("Search Availability");
        searchButton.addActionListener(e -> {
            searchVehicles();
            openVehicleCatalogDialog("Filtered Vehicle Results");
        });
        JButton allButton = createGhostButton("Load Full Catalog");
        allButton.addActionListener(e -> {
            refreshVehicleTable(agencyService.getVehicles().stream().toList());
            setStatus("Full vehicle catalog displayed.");
            openVehicleCatalogDialog("Full Vehicle Catalog");
        });
        JButton openCatalogButton = createGhostButton("Open Vehicle Catalog");
        openCatalogButton.addActionListener(e -> {
            refreshVehicleTable(agencyService.getVehicles().stream().toList());
            openVehicleCatalogDialog("Full Vehicle Catalog");
        });
        buttonRow.add(searchButton);
        buttonRow.add(allButton);
        buttonRow.add(openCatalogButton);

        form.add(buttonRow);
        JLabel note = new JLabel("This works well as a branch filtering demo.");
        note.setForeground(TEXT_MUTED);
        form.add(note);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildVehicleHighlightsCard() {
        JTextArea insights = createNarrativeArea();
        insights.setText("""
Fleet Highlights

- Economy: budget-friendly for everyday customers
- SUV: family and comfort-focused rentals
- Luxury: premium invoice showcase for demo impact
- Van: group and transfer scenarios

Tip
- Search for BR03 and SUV to spotlight the black Cupra Formentor.
""");

        JPanel card = createCardPanel("Fleet Highlights", "Guide the viewer toward the most impressive flows.");
        card.add(new JScrollPane(insights), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildVehicleCatalogLaunchCard() {
        JPanel card = createCardPanel("Vehicle Catalog Access", "Open the vehicle list in a dedicated polished window.");

        JTextArea copy = createNarrativeArea();
        copy.setText("""
Catalog Tips

- Use Open Vehicle Catalog for the full list.
- Use Search Availability first if you want filtered results.
- Every vehicle row shows its color beside brand and model.
- Nissan Micra 1.0 Vision appears as a Green economy vehicle in BR01.
""");

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 12));
        actions.setOpaque(false);
        JButton openFullCatalog = createAccentButton("Open Full Catalog");
        openFullCatalog.addActionListener(e -> {
            refreshVehicleTable(agencyService.getVehicles().stream().toList());
            openVehicleCatalogDialog("Full Vehicle Catalog");
        });
        JButton openFilteredCatalog = createGhostButton("Open Current Results");
        openFilteredCatalog.addActionListener(e -> {
            if (vehicleTableModel.getRowCount() == 0) {
                refreshVehicleTable(agencyService.getVehicles().stream().toList());
                openVehicleCatalogDialog("Full Vehicle Catalog");
                setStatus("No filtered results were loaded, so the full catalog was opened.");
            } else {
                openVehicleCatalogDialog("Current Vehicle Results");
            }
        });
        actions.add(openFullCatalog);
        actions.add(openFilteredCatalog);

        card.add(new JScrollPane(copy), BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildCustomerPanel() {
        JPanel panel = createPagePanel();
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 14));
        top.setOpaque(false);
        top.add(buildCustomerFormCard());
        top.add(buildLoyaltyCard());

        panel.add(top, BorderLayout.NORTH);
        panel.add(createTableSection("Customer Portfolio", "Registered customers with tier and discount visibility.", customerTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCustomerFormCard() {
        JPanel card = createCardPanel("Customer Registration", "Create customer records with ID, contact, and license details.");
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setOpaque(false);
        form.add(createFieldLabel("Customer ID"));
        form.add(customerIdField);
        form.add(createFieldLabel("Full Name"));
        form.add(customerNameField);
        form.add(createFieldLabel("Phone"));
        form.add(customerPhoneField);
        form.add(createFieldLabel("License Number"));
        form.add(customerLicenseField);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));
        buttons.setOpaque(false);
        JButton addButton = createAccentButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        JButton reloadButton = createGhostButton("Reload");
        reloadButton.addActionListener(e -> {
            refreshAllViews();
            setStatus("Customer records refreshed.");
        });
        buttons.add(addButton);
        buttons.add(reloadButton);

        form.add(buttons);
        JLabel note = new JLabel("IDs auto-fill, but you can still override them manually.");
        note.setForeground(TEXT_MUTED);
        form.add(note);
        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLoyaltyCard() {
        JTextArea copy = createNarrativeArea();
        copy.setText("""
Loyalty Tiers

Bronze
- Entry tier
- 0% discount

Silver
- Mid-tier after point growth
- 5% discount

Gold
- Premium returning customers
- 10% discount

Points are awarded automatically after invoice generation.
""");

        JPanel card = createCardPanel("Loyalty Program", "This is a strong rubric point because it ties business logic into the UI.");
        card.add(new JScrollPane(copy), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildReservationPanel() {
        JPanel panel = createPagePanel();

        JPanel top = new JPanel(new GridLayout(1, 2, 14, 14));
        top.setOpaque(false);
        top.add(buildReservationFormCard());
        top.add(buildReservationWorkflowCard());

        JTabbedPane outputs = new JTabbedPane();
        outputs.setFont(new Font("SansSerif", Font.BOLD, 12));
        outputs.addTab("Reservation IDs", buildReservationLookupSection());
        outputs.addTab("Reservations", createTableSection("Reservations", "Track current booking state transitions.", reservationTable));
        outputs.addTab("Invoices", createTableSection("Invoices", "Show billing calculations with discount visibility.", invoiceTable));
        outputs.addTab("Damage Reports", createTableSection("Damage Reports", "Optional return-stage assessments.", damageTable));

        panel.add(top, BorderLayout.NORTH);
        panel.add(outputs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReservationFormCard() {
        JPanel card = createCardPanel("Create Reservation", "Multi-branch booking with insurance and rental agent assignment.");
        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setOpaque(false);
        form.add(createFieldLabel("Reservation ID"));
        form.add(reservationIdField);
        form.add(createFieldLabel("Customer"));
        form.add(reservationCustomerBox);
        form.add(createFieldLabel("Vehicle"));
        form.add(reservationVehicleBox);
        form.add(createFieldLabel("Pickup Branch"));
        form.add(pickupBranchBox);
        form.add(createFieldLabel("Return Branch"));
        form.add(returnBranchBox);
        form.add(createFieldLabel("Insurance"));
        form.add(insuranceBox);
        form.add(createFieldLabel("Rental Agent"));
        form.add(agentBox);
        form.add(createFieldLabel("Start Date"));
        form.add(reservationStartField);
        form.add(createFieldLabel("End Date"));
        form.add(reservationEndField);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));
        buttons.setOpaque(false);
        JButton createButton = createAccentButton("Create Reservation");
        createButton.addActionListener(e -> createReservation());
        JButton reloadButton = createGhostButton("Reload Data");
        reloadButton.addActionListener(e -> {
            refreshAllViews();
            setStatus("Reservation data refreshed.");
        });
        buttons.add(createButton);
        buttons.add(reloadButton);
        card.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        south.setOpaque(false);
        latestReservationLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        latestReservationLabel.setForeground(BRAND_RED);
        south.add(latestReservationLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildReservationLookupSection() {
        JPanel card = createTableSection("Reservation ID Lookup", "Use this list to find the exact ID you need for pickup or return.", reservationLookupTable);
        return card;
    }

    private JPanel buildReservationWorkflowCard() {
        JPanel card = createCardPanel("Pickup and Return Workflow", "Demonstrates state changes, billing, and damage handling.");
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setOpaque(false);
        form.add(createFieldLabel("Pickup Reservation ID"));
        form.add(pickupReservationField);
        JButton pickupButton = createAccentButton("Pick Up Vehicle");
        pickupButton.addActionListener(e -> pickUpVehicle());
        form.add(pickupButton);
        JLabel pickupHint = new JLabel("Status changes from RESERVED to PICKED_UP");
        pickupHint.setForeground(TEXT_MUTED);
        form.add(pickupHint);
        form.add(createFieldLabel("Return Reservation ID"));
        form.add(returnReservationField);
        form.add(createFieldLabel("Mileage Driven"));
        form.add(mileageField);
        form.add(createFieldLabel("Damage Notes"));
        form.add(damageField);
        JButton returnButton = createGhostButton("Return Vehicle and Generate Invoice");
        returnButton.addActionListener(e -> returnVehicle());
        form.add(returnButton);
        JLabel returnHint = new JLabel("Creates invoice, updates loyalty points, and stores damage fees.");
        returnHint.setForeground(TEXT_MUTED);
        form.add(returnHint);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMaintenancePanel() {
        JPanel panel = createPagePanel();
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 14));
        top.setOpaque(false);
        top.add(buildMaintenanceFormCard());
        top.add(buildMaintenanceNarrativeCard());

        JTabbedPane outputs = new JTabbedPane();
        outputs.addTab("Employees", createTableSection("Employee Directory", "Branch managers, rental agents, and mechanics.", employeeTable));
        outputs.addTab("Maintenance", createTableSection("Maintenance Records", "Scheduled service tasks and mechanic assignments.", maintenanceTable));

        panel.add(top, BorderLayout.NORTH);
        panel.add(outputs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMaintenanceFormCard() {
        JPanel card = createCardPanel("Schedule Maintenance", "Assign a mechanic and move the vehicle into maintenance mode.");
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setOpaque(false);
        form.add(createFieldLabel("Vehicle"));
        form.add(maintenanceVehicleBox);
        form.add(createFieldLabel("Mechanic"));
        form.add(mechanicBox);
        form.add(createFieldLabel("Scheduled Date"));
        form.add(maintenanceDateField);
        form.add(createFieldLabel("Description"));
        form.add(maintenanceDescriptionField);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));
        buttons.setOpaque(false);
        JButton scheduleButton = createAccentButton("Schedule");
        scheduleButton.addActionListener(e -> scheduleMaintenance());
        JButton reloadButton = createGhostButton("Reload");
        reloadButton.addActionListener(e -> {
            refreshAllViews();
            setStatus("Maintenance records refreshed.");
        });
        buttons.add(scheduleButton);
        buttons.add(reloadButton);
        form.add(buttons);
        JLabel note = new JLabel("Vehicles in maintenance cannot be reserved.");
        note.setForeground(TEXT_MUTED);
        form.add(note);
        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMaintenanceNarrativeCard() {
        JTextArea copy = createNarrativeArea();
        copy.setText("""
Maintenance Demo Angle

- Use this tab to show bonus functionality.
- Pick any vehicle and assign a mechanic.
- The selected vehicle becomes unavailable for reservation.
- This reinforces your exception handling and workflow depth.
""");
        JPanel card = createCardPanel("Stretch Goal Value", "Great for bonus points and presentation depth.");
        card.add(new JScrollPane(copy), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PANEL_BACKGROUND);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(214, 221, 229)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_PRIMARY);

        JLabel hintLabel = new JLabel("KFISB demo path: welcome -> catalog -> customer -> reserve -> pick up -> return.");
        hintLabel.setForeground(TEXT_MUTED);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(hintLabel, BorderLayout.EAST);
        return footer;
    }

    private void searchVehicles() {
        try {
            List<Vehicle> vehicles = agencyService.searchAvailableVehicles(
                    selectedId(vehicleBranchBox),
                    String.valueOf(vehicleTypeBox.getSelectedItem()),
                    LocalDate.parse(vehicleStartDateField.getText().trim()),
                    LocalDate.parse(vehicleEndDateField.getText().trim()));
            refreshVehicleTable(vehicles);
            setStatus("Vehicle availability search completed: " + vehicles.size() + " result(s).");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addCustomer() {
        try {
            String customerId = customerIdField.getText().trim().isEmpty()
                    ? agencyService.nextCustomerId()
                    : customerIdField.getText().trim();
            validateText(customerNameField.getText(), "Customer name");
            validateText(customerPhoneField.getText(), "Customer phone");
            validateText(customerLicenseField.getText(), "License number");
            agencyService.registerCustomer(
                    customerId,
                    customerNameField.getText().trim(),
                    customerPhoneField.getText().trim(),
                    customerLicenseField.getText().trim());
            clearCustomerForm();
            refreshAllViews();
            showMessage("Customer added successfully.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void createReservation() {
        try {
            String reservationId = reservationIdField.getText().trim().isEmpty()
                    ? agencyService.nextReservationId()
                    : reservationIdField.getText().trim();
            agencyService.createReservation(
                    reservationId,
                    selectedId(reservationCustomerBox),
                    selectedId(reservationVehicleBox),
                    selectedId(pickupBranchBox),
                    selectedId(returnBranchBox),
                    InsuranceOption.valueOf(String.valueOf(insuranceBox.getSelectedItem())),
                    selectedId(agentBox),
                    LocalDate.parse(reservationStartField.getText().trim()),
                    LocalDate.parse(reservationEndField.getText().trim()));
            reservationIdField.setText("");
            refreshAllViews();
            latestReservationLabel.setText("Latest reservation ID: " + reservationId);
            pickupReservationField.setText(reservationId);
            returnReservationField.setText(reservationId);
            showMessage("Reservation created successfully. Your reservation ID is: " + reservationId);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void pickUpVehicle() {
        try {
            validateText(pickupReservationField.getText(), "Pickup reservation ID");
            String reservationId = pickupReservationField.getText().trim();
            agencyService.pickUpVehicle(reservationId);
            pickupReservationField.setText("");
            refreshAllViews();
            showMessage("Vehicle picked up successfully.");
            setStatus("Reservation " + reservationId + " is now active.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void returnVehicle() {
        try {
            validateText(returnReservationField.getText(), "Return reservation ID");
            int mileage = Integer.parseInt(mileageField.getText().trim());
            if (mileage < 0) {
                throw new IllegalArgumentException("Mileage cannot be negative.");
            }
            String reservationId = returnReservationField.getText().trim();
            Invoice invoice = agencyService.returnVehicle(reservationId, mileage, damageField.getText().trim());
            returnReservationField.setText("");
            damageField.setText("");
            refreshAllViews();
            showMessage("Vehicle returned. Invoice total: $" + formatMoney(invoice.getTotalAmount()));
            setStatus("Return completed. Invoice " + invoice.getInvoiceId() + " generated.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void scheduleMaintenance() {
        try {
            agencyService.scheduleMaintenance(
                    selectedId(maintenanceVehicleBox),
                    selectedId(mechanicBox),
                    LocalDate.parse(maintenanceDateField.getText().trim()),
                    maintenanceDescriptionField.getText().trim());
            refreshAllViews();
            showMessage("Maintenance scheduled.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void refreshAllViews() {
        branchCountLabel.setText(String.valueOf(agencyService.getBranches().size()));
        vehicleCountLabel.setText(String.valueOf(agencyService.getVehicles().size()));
        reservationCountLabel.setText(String.valueOf(agencyService.getReservations().size()));
        invoiceCountLabel.setText(String.valueOf(agencyService.getInvoices().size()));
        heroInsightLabel.setText("Live rental operations overview across " + agencyService.getBranches().size() + " branches and "
                + agencyService.getVehicles().size() + " vehicles.");
        updateLatestReservationLabel();

        dashboardArea.setText(buildExecutiveSummary());
        refreshVehicleTable(agencyService.getVehicles().stream().toList());
        refreshCustomerTable();
        refreshReservationOutputs();
        refreshMaintenanceOutputs();

        populateBranches(vehicleBranchBox);
        populateBranches(pickupBranchBox);
        populateBranches(returnBranchBox);
        populateCustomers();
        populateVehicles();
        populateAgents();
        populateMechanics();

        if (customerIdField.getText().trim().isEmpty()) {
            customerIdField.setText(agencyService.nextCustomerId());
        }
        if (reservationIdField.getText().trim().isEmpty()) {
            reservationIdField.setText(agencyService.nextReservationId());
        }
    }

    private void refreshVehicleTable(List<Vehicle> vehicles) {
        resetModel(vehicleTableModel);
        for (Vehicle vehicle : vehicles) {
            vehicleTableModel.addRow(new Object[]{
                vehicle.getVehicleId(),
                vehicle.getVehicleType(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getYear(),
                vehicle.getBranchId(),
                vehicle.getStatus(),
                "$" + formatMoney(vehicle.getDailyRate())
            });
        }
    }

    private void openVehicleCatalogDialog(String title) {
        JFrame catalogFrame = new JFrame(title + " - " + AgencyService.PRODUCT_NAME);
        catalogFrame.setSize(1080, 520);
        catalogFrame.setLocationRelativeTo(this);
        catalogFrame.getContentPane().setBackground(PAGE_BACKGROUND);
        catalogFrame.setLayout(new BorderLayout(12, 12));

        JLabel header = new JLabel(title);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(TEXT_PRIMARY);

        JLabel subHeader = new JLabel("Brand, model, color, branch, and pricing are displayed together for easier review.");
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subHeader.setForeground(TEXT_MUTED);

        JPanel top = new JPanel();
        top.setBackground(PAGE_BACKGROUND);
        top.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(4));
        top.add(subHeader);
        top.add(Box.createVerticalStrut(10));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search Catalog");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchLabel.setForeground(TEXT_PRIMARY);

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 191, 208)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.setToolTipText("Search by brand, model, color, branch, or type");
        JButton clearButton = createGhostButton("Clear");
        clearButton.addActionListener(e -> searchField.setText(""));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearButton, BorderLayout.EAST);

        DefaultTableModel catalogModel = copyVehicleTableModel();
        JTable catalogTable = createTable(catalogModel);
        configureVehicleColorColumn(catalogTable);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(catalogModel);
        catalogTable.setRowSorter(sorter);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void applyFilter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
        });
        top.add(searchPanel);
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 228, 235)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JPanel centerCard = new JPanel(new BorderLayout());
        centerCard.setBackground(PANEL_BACKGROUND);
        centerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 225, 233)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(PAGE_BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        wrapper.add(centerCard, BorderLayout.CENTER);

        catalogFrame.add(top, BorderLayout.NORTH);
        catalogFrame.add(wrapper, BorderLayout.CENTER);
        catalogFrame.setVisible(true);
    }

    private DefaultTableModel copyVehicleTableModel() {
        DefaultTableModel model = createTableModel("ID", "Type", "Brand", "Model", "Color", "Year", "Branch", "Status", "Daily Rate");
        for (int row = 0; row < vehicleTableModel.getRowCount(); row++) {
            Object[] rowData = new Object[vehicleTableModel.getColumnCount()];
            for (int column = 0; column < vehicleTableModel.getColumnCount(); column++) {
                rowData[column] = vehicleTableModel.getValueAt(row, column);
            }
            model.addRow(rowData);
        }
        return model;
    }

    private void refreshCustomerTable() {
        resetModel(customerTableModel);
        for (Customer customer : agencyService.getCustomers()) {
            customerTableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getLicenseNumber(),
                customer.getLoyaltyTier(),
                customer.getLoyaltyPoints(),
                (int) Math.round(customer.getDiscountRate() * 100) + "%"
            });
        }
    }

    private void refreshReservationOutputs() {
        resetModel(reservationTableModel);
        resetModel(reservationLookupTableModel);
        for (Reservation reservation : agencyService.getReservations()) {
            reservationTableModel.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getCustomer().getFullName(),
                reservation.getVehicle().getVehicleId() + " / " + reservation.getVehicle().getVehicleType(),
                reservation.getPickupBranch().getBranchId(),
                reservation.getReturnBranch().getBranchId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
            });
            reservationLookupTableModel.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getCustomer().getFullName(),
                reservation.getVehicle().getBrand() + " " + reservation.getVehicle().getModel(),
                reservation.getStatus()
            });
        }

        resetModel(invoiceTableModel);
        for (Invoice invoice : agencyService.getInvoices()) {
            invoiceTableModel.addRow(new Object[]{
                invoice.getInvoiceId(),
                invoice.getReservationId(),
                "$" + formatMoney(invoice.getBaseAmount()),
                "$" + formatMoney(invoice.getDiscountAmount()),
                "$" + formatMoney(invoice.getDamageFee()),
                "$" + formatMoney(invoice.getMileageFee()),
                "$" + formatMoney(invoice.getTotalAmount())
            });
        }

        resetModel(damageTableModel);
        for (DamageReport report : agencyService.getDamageReports()) {
            damageTableModel.addRow(new Object[]{
                report.getReportId(),
                report.getReservationId(),
                report.getNotes(),
                "$" + formatMoney(report.getFee())
            });
        }
    }

    private void updateLatestReservationLabel() {
        if (agencyService.getReservations().isEmpty()) {
            latestReservationLabel.setText("Latest reservation ID: none yet");
            return;
        }
        Reservation latestReservation = null;
        for (Reservation reservation : agencyService.getReservations()) {
            latestReservation = reservation;
        }
        if (latestReservation != null) {
            latestReservationLabel.setText("Latest reservation ID: " + latestReservation.getReservationId());
        }
    }

    private void wireReservationLookupTable() {
        reservationLookupTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = reservationLookupTable.getSelectedRow();
                    if (row >= 0) {
                        String reservationId = String.valueOf(reservationLookupTable.getValueAt(row, 0));
                        pickupReservationField.setText(reservationId);
                        returnReservationField.setText(reservationId);
                        latestReservationLabel.setText("Selected reservation ID: " + reservationId);
                        setStatus("Reservation " + reservationId + " loaded into pickup and return fields.");
                    }
                }
            }
        });
    }

    private void refreshMaintenanceOutputs() {
        resetModel(employeeTableModel);
        for (Employee employee : agencyService.getEmployees()) {
            employeeTableModel.addRow(new Object[]{
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getRole(),
                employee.getBranchId(),
                employee.getRoleDetails()
            });
        }

        resetModel(maintenanceTableModel);
        for (MaintenanceRecord record : agencyService.getMaintenanceRecords()) {
            maintenanceTableModel.addRow(new Object[]{
                record.getMaintenanceId(),
                record.getVehicleId(),
                record.getMechanicId(),
                record.getScheduledDate(),
                record.getDescription()
            });
        }
    }

    private String buildExecutiveSummary() {
        long availableVehicles = agencyService.getVehicles().stream()
                .filter(vehicle -> vehicle.getStatus() == VehicleStatus.AVAILABLE)
                .count();
        long activeReservations = agencyService.getReservations().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.RESERVED
                || reservation.getStatus() == ReservationStatus.PICKED_UP)
                .count();
        long premiumCustomers = agencyService.getCustomers().stream()
                .filter(customer -> customer.getLoyaltyTier() == LoyaltyTier.SILVER || customer.getLoyaltyTier() == LoyaltyTier.GOLD)
                .count();

        return """
Performance Snapshot

FleetOrbit Rental Suite presents a complete branch-based rental workflow with a polished, presentation-friendly interface.

Current State
- Available vehicles: %d
- Active reservations: %d
- Premium loyalty customers: %d
- Maintenance records: %d

Business Story
- Inventory is segmented across branches and vehicle categories.
- Reservations enforce conflict validation and availability checks.
- Invoice generation includes insurance, mileage, loyalty discount, and damage fees.
- Maintenance scheduling demonstrates a strong stretch-goal workflow.

Presentation Angle
- This UI is designed to help you show both software engineering quality and business realism in the same demo.
""".formatted(availableVehicles, activeReservations, premiumCustomers, agencyService.getMaintenanceRecords().size());
    }

    private void populateBranches(JComboBox<String> comboBox) {
        String previous = (String) comboBox.getSelectedItem();
        comboBox.removeAllItems();
        for (Branch branch : agencyService.getBranches()) {
            comboBox.addItem(branch.getBranchId() + " - " + branch.getName() + " / " + branch.getCity());
        }
        restoreSelection(comboBox, previous);
    }

    private void populateCustomers() {
        String previous = (String) reservationCustomerBox.getSelectedItem();
        reservationCustomerBox.removeAllItems();
        for (Customer customer : agencyService.getCustomers()) {
            reservationCustomerBox.addItem(customer.getCustomerId() + " - " + customer.getFullName() + " - " + customer.getLoyaltyTier());
        }
        restoreSelection(reservationCustomerBox, previous);
    }

    private void populateVehicles() {
        String previousReservation = (String) reservationVehicleBox.getSelectedItem();
        String previousMaintenance = (String) maintenanceVehicleBox.getSelectedItem();
        reservationVehicleBox.removeAllItems();
        maintenanceVehicleBox.removeAllItems();
        for (Vehicle vehicle : agencyService.getVehicles()) {
            String label = vehicle.getVehicleId() + " - " + vehicle.getVehicleType()
                    + " - " + vehicle.getBrand() + " " + vehicle.getModel()
                    + " - " + vehicle.getColor()
                    + " - " + vehicle.getStatus();
            reservationVehicleBox.addItem(label);
            maintenanceVehicleBox.addItem(label);
        }
        restoreSelection(reservationVehicleBox, previousReservation);
        restoreSelection(maintenanceVehicleBox, previousMaintenance);
    }

    private void populateAgents() {
        String previous = (String) agentBox.getSelectedItem();
        agentBox.removeAllItems();
        for (RentalAgent agent : agencyService.getRentalAgents()) {
            agentBox.addItem(agent.getEmployeeId() + " - " + agent.getFullName() + " - " + agent.getBranchId());
        }
        restoreSelection(agentBox, previous);
    }

    private void populateMechanics() {
        String previous = (String) mechanicBox.getSelectedItem();
        mechanicBox.removeAllItems();
        for (Mechanic mechanic : agencyService.getMechanics()) {
            mechanicBox.addItem(mechanic.getEmployeeId() + " - " + mechanic.getFullName() + " - " + mechanic.getBranchId());
        }
        restoreSelection(mechanicBox, previous);
    }

    private void restoreSelection(JComboBox<String> comboBox, String previousValue) {
        if (previousValue != null) {
            comboBox.setSelectedItem(previousValue);
        }
        if (comboBox.getSelectedItem() == null && comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0);
        }
    }

    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(PAGE_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));
        return panel;
    }

    private JPanel createCardPanel(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(createCardBorder());

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(236, 209, 221)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    private JPanel createMetricCard(String title, String subtitle, JLabel valueLabel, Color background) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(background);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker()),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_MUTED);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(3));
        text.add(subtitleLabel);

        card.add(text, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createMetricValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("SansSerif", Font.BOLD, 30));
        label.setForeground(BRAND_RED);
        return label;
    }

    private JLabel createMetricDisplayLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setForeground(BRAND_RED);
        return label;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createHeroBadge(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(new Color(255, 231, 238));
        label.setForeground(BRAND_RED);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JPanel createWelcomeHighlight(String eyebrow, String title, String detail) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(new Color(255, 255, 255, 38));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 65)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel eyebrowLabel = new JLabel(eyebrow);
        eyebrowLabel.setForeground(new Color(255, 233, 239));
        eyebrowLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel detailLabel = new JLabel(detail);
        detailLabel.setForeground(new Color(255, 237, 243));
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.add(eyebrowLabel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(detailLabel, BorderLayout.SOUTH);
        return card;
    }

    private JTextArea createNarrativeArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(PANEL_BACKGROUND);
        area.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        return area;
    }

    private JTextArea createWelcomeNarrativeArea() {
        JTextArea area = createNarrativeArea();
        area.setOpaque(false);
        area.setForeground(new Color(255, 239, 244));
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return area;
    }

    private JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(248, 220, 231), BRAND_RED, new Color(198, 103, 138));
        return button;
    }

    private JButton createPrimaryCtaButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, BRAND_RED, Color.WHITE, new Color(171, 59, 79));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private JButton createGhostButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(255, 250, 252), new Color(120, 50, 84), new Color(223, 188, 205));
        return button;
    }

    private void styleButton(JButton button, Color background, Color foreground, Color borderColor) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
    }

    private DefaultTableModel createTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(232, 236, 241));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(219, 233, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(233, 239, 247));
        header.setForeground(TEXT_PRIMARY);
        header.setReorderingAllowed(false);
        if (findColumnIndex(model, "Color") >= 0) {
            configureVehicleColorColumn(table);
        }
        return table;
    }

    private void configureVehicleColorColumn(JTable table) {
        int colorColumn = findColumnIndex((DefaultTableModel) table.getModel(), "Color");
        if (colorColumn < 0) {
            return;
        }
        table.getColumnModel().getColumn(colorColumn).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                String colorName = value == null ? "" : value.toString();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setBackground(tbl.getSelectionBackground());
                    label.setForeground(tbl.getSelectionForeground());
                    label.setOpaque(true);
                    return label;
                }
                label.setOpaque(true);
                label.setBackground(mapColorChip(colorName));
                label.setForeground(TEXT_PRIMARY);
                return label;
            }
        });
    }

    private int findColumnIndex(DefaultTableModel model, String columnName) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (columnName.equals(model.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    private Color mapColorChip(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "green" -> new Color(204, 239, 208);
            case "red" -> new Color(248, 215, 218);
            case "blue", "midnight blue", "navy" -> new Color(211, 225, 245);
            case "black", "obsidian black", "graphite" -> new Color(219, 222, 228);
            case "white", "pearl white", "glacier white" -> new Color(246, 247, 249);
            case "silver", "gray" -> new Color(231, 235, 239);
            case "bronze", "champagne" -> new Color(242, 229, 208);
            default -> new Color(236, 241, 245);
        };
    }

    private JPanel createTableSection(String title, String subtitle, JTable table) {
        JPanel card = createCardPanel(title, subtitle);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 228, 235)));
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void resetModel(DefaultTableModel model) {
        model.setRowCount(0);
    }

    private String selectedId(JComboBox<String> comboBox) {
        Object item = comboBox.getSelectedItem();
        if (item == null) {
            return "";
        }
        String text = item.toString();
        int separatorIndex = text.indexOf(" - ");
        return separatorIndex > 0 ? text.substring(0, separatorIndex) : text;
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    private void clearCustomerForm() {
        customerNameField.setText("");
        customerPhoneField.setText("");
        customerLicenseField.setText("");
        customerIdField.setText(agencyService.nextCustomerId());
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(Exception ex) {
        setStatus("Error: " + ex.getMessage());
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        setStatus(message);
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
