/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package View;

import DatabaseConfig.Database;
import Model.Car;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.*;
import java.util.Vector;

// Import FlatLaf themes
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 *
 * @author nikwn
 */

public class ViewCarsGUI extends JFrame {
    private final Database database;
    private final JLabel imageLabel;
    private final JTable carTable;
    private final DefaultTableModel tableModel;

    // Define the PURPLE color consistent with CarRentalApp
    private static final Color PURPLE = new Color(0x9c84bc);

    public ViewCarsGUI(Database database) {
        this.database = database;

        // --- Apply global UI properties matching the CarRentalApp login theme ---
        // These settings MUST be applied BEFORE setting the Look and Feel
        UIManager.put("Button.arc", 20);
        UIManager.put("Component.arc", 20);
        UIManager.put("Component.focusColor", PURPLE);
        UIManager.put("Button.focusColor", new Color(0, 0, 0, 0)); // No focus indication for buttons
        UIManager.put("Button.default.focusColor", new Color(0, 0, 0, 0));
        UIManager.put("Button.hoverBackground", PURPLE);
        UIManager.put("Button.hoverForeground", Color.WHITE);
        UIManager.put("Button.default.background", PURPLE); // Default buttons will be purple
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.borderColor", PURPLE);

        // --- Set the FlatLaf macOS Light theme ---
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf()); // Changed to FlatMacLightLaf
            FlatLaf.setUseNativeWindowDecorations(true); // For consistent window decorations
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf macOS Light theme. Using fallback.");
            try {
                // Fallback to Nimbus if FlatLaf is not available or fails to load
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not set Nimbus Look and Feel: " + ex.getMessage());
            }
        }

        setTitle("View All Cars");
        setSize(1100, 800);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- Main Content Panel with Generous Padding ---
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBorder(new EmptyBorder(20, 30, 30, 30));
        add(mainPanel);

        // --- 2. Split Pane for Image Preview and Table ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(10);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // --- Top Component of Split Pane: Image Preview ---
        JPanel imagePreviewPanel = new JPanel(new BorderLayout());
        imagePreviewPanel.setBackground(Color.WHITE); // Explicit white background for panel
        imagePreviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(600, 300));
        imageLabel.setMinimumSize(new Dimension(300, 150));
        imageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        imageLabel.setBackground(new Color(240, 240, 240)); // Light grey background if no image
        imageLabel.setOpaque(true);
        imagePreviewPanel.add(imageLabel, BorderLayout.CENTER);

        splitPane.setTopComponent(imagePreviewPanel);

        // --- Bottom Component of Split Pane: Car Table ---
        JPanel tableContainerPanel = new JPanel(new BorderLayout());
        tableContainerPanel.setBackground(Color.WHITE); // Explicit white background for panel
        tableContainerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columnNames = {"ID", "Brand", "Model", "Year", "Fuel", "Gearbox", "Engine CC", "HP", "Consumption", "Price/Day", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: case 3: case 6: case 7: case 10: return Integer.class;
                    default: return String.class;
                }
            }
        };
        carTable = new JTable(tableModel);
        carTable.setFillsViewportHeight(true);
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        carTable.setRowHeight(30);
        carTable.setIntercellSpacing(new Dimension(0, 5));

        JTableHeader tableHeader = carTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableHeader.setBackground(new Color(235, 235, 235)); // Light gray header
        tableHeader.setForeground(new Color(70, 70, 70)); // Darker text for header
        tableHeader.setReorderingAllowed(false);

        carTable.setGridColor(new Color(230, 230, 230));
        carTable.setShowVerticalLines(false);
        carTable.setRowSelectionAllowed(true);
        carTable.setColumnSelectionAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        carTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        carTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        carTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        carTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        carTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);

        carTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = carTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayCarImage(selectedRow);
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(carTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tableContainerPanel.add(tableScrollPane, BorderLayout.CENTER);
        splitPane.setBottomComponent(tableContainerPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        loadCarsIntoTable();
        
        if (tableModel.getRowCount() > 0) {
            carTable.setRowSelectionInterval(0, 0);
            displayCarImage(0);
        } else {
            imageLabel.setIcon(null);
        }

        setVisible(true);
    }

    private void loadCarsIntoTable() {
        tableModel.setRowCount(0);
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars ORDER BY Brand, Model, Year");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getString("Brand"));
                row.add(rs.getString("Model"));
                row.add(rs.getInt("Year"));
                row.add(rs.getString("Fuel"));
                row.add(rs.getString("Gearbox"));
                row.add(rs.getInt("EngineCC"));
                row.add(rs.getInt("Horsepower"));
                row.add(String.format("%.1f L/100km", rs.getFloat("Consumption")));
                row.add(String.format("%.2f €", rs.getFloat("Price")));
                if(rs.getInt("Available") == 1){
                    row.add("Available");
                }else{
                    row.add("Not Available");
                }
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load cars into table.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayCarImage(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            imageLabel.setIcon(null);
            return;
        }

        int carId = (int) tableModel.getValueAt(row, 0); 
        String imagePath = null;
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT image_path FROM cars WHERE ID = ?")) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                imagePath = rs.getString("image_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching image path for car ID: " + carId);
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                URL imageURL = getClass().getClassLoader().getResource("resources/" + imagePath);
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image img = icon.getImage();
                    
                    int labelWidth = imageLabel.getWidth() > 0 ? imageLabel.getWidth() : imageLabel.getPreferredSize().width;
                    int labelHeight = imageLabel.getHeight() > 0 ? imageLabel.getHeight() : imageLabel.getPreferredSize().height;
                    
                    double imgRatio = (double) img.getWidth(null) / img.getHeight(null);
                    double labelRatio = (double) labelWidth / labelHeight;
                    
                    int newWidth, newHeight;
                    if (imgRatio > labelRatio) {
                        newWidth = labelWidth;
                        newHeight = (int) (labelWidth / imgRatio);
                    } else {
                        newHeight = labelHeight;
                        newWidth = (int) (labelHeight * imgRatio);
                    }
                    
                    if (newWidth > labelWidth) newWidth = labelWidth;
                    if (newHeight > labelHeight) newHeight = labelHeight;

                    Image scaled = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } else {
                    imageLabel.setIcon(null);
                    System.err.println("Image not found at path: resources/" + imagePath);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                imageLabel.setIcon(null);
                System.err.println("Error loading image for path: " + imagePath);
            }
        } else {
            imageLabel.setIcon(null); 
            System.out.println("No image path found or path is empty for car ID: " + carId);
        }
    }
}