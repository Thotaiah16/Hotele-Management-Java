package hotel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllBookingsView extends JFrame {

    private final JTable bookingsTable;
    private JPanel contentPane;

    public AllBookingsView() {
        setTitle("All Customer Booking Details - Hotel RAAR Admin Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 700); // Adjusted width
        setLocationRelativeTo(null);

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 235, 255), 0, getHeight(), new Color(245, 250, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(22, 160, 133));
        headerPanel.setBounds(0, 0, 1200, 80);
        headerPanel.setLayout(null);
        contentPane.add(headerPanel);

        JLabel lblTitle = new JLabel("ALL CUSTOMER BOOKINGS (Desktop & Online)");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 15, 800, 35);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Consolidated view of all registered guests");
        lblSubtitle.setFont(new Font("Tahoma", Font.ITALIC, 16));
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setBounds(30, 50, 400, 20);
        headerPanel.add(lblSubtitle);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 110, 925, 520); // Adjusted width
        contentPane.add(scrollPane);

        bookingsTable = new JTable();
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bookingsTable.setRowHeight(28);
        bookingsTable.setGridColor(Color.LIGHT_GRAY);
        bookingsTable.setSelectionBackground(new Color(173, 216, 230));

        JTableHeader tableHeader = bookingsTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        tableHeader.setBackground(new Color(70, 130, 180));
        tableHeader.setForeground(Color.WHITE);

        scrollPane.setViewportView(bookingsTable);

        loadAllBookings();
    }

    private void loadAllBookings() {
        // ✅ UPDATED: Removed "Address" and "Bed Type" from headers
        String[] columnNames = {"Customer Name", "Phone", "Room No.", "Total Price", "Source"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // ✅ UPDATED: Removed address and bedtype columns from the SQL query
        String sql = 
            "SELECT " +
            "    c.Name, " +
            "    c.Phone, " +
            "    rc.roomno, " +
            "    rc.price, " +
            "    'Desktop' AS source " +
            "FROM customer c JOIN roomcutomer rc ON c.Name = rc.name " +
            
            "UNION ALL " +

            "SELECT " +
            "    u.username, " +
            "    u.mobile_number, " +
            "    b.room_no, " +
            "    b.total_price, " +
            "    'Online' AS source " +
            "FROM bookings b JOIN users u ON b.user_id = u.id";

        try (Connection conn = new GetConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // ✅ UPDATED: Removed address and bedType variables
                // The column names here are taken from the FIRST SELECT statement
                String name = rs.getString("Name");
                String phone = rs.getString("Phone");
                String roomNo = rs.getString("roomno");
                String price = "₹" + rs.getString("price");
                String source = rs.getString("source");

                // ✅ UPDATED: Added the correct variables to the row
                model.addRow(new Object[]{name, phone, roomNo, price, source});
            }

            bookingsTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "A database error occurred. Please double-check your database connection and table structures.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}