package src.service;

import src.db.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class AdminService {

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username=? AND password=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Login error:");
            e.printStackTrace();
        }
        return false;
    }

    public void addAdmin(String username, String password) {
        String sql = "INSERT INTO admins(username, password) VALUES(?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            System.out.println("Admin added successfully!");

        } catch (Exception e) {
            System.out.println("Add admin failed.");
            e.printStackTrace();
        }
    }

    public void updateAdmin(int id, String username, String password) {
        String sql = "UPDATE admins SET username=?, password=? WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, id);

            ps.executeUpdate();
            System.out.println("Admin updated successfully!");

        } catch (Exception e) {
            System.out.println("Update admin failed.");
            e.printStackTrace();
        }
    }

    public void deleteAdmin(int id) {
        String sql = "DELETE FROM admins WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("Admin deleted successfully!");

        } catch (Exception e) {
            System.out.println("Delete admin failed.");
            e.printStackTrace();
        }
    }

    public void viewAdmins() {
        String sql = "SELECT * FROM admins";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- ADMIN ACCOUNTS ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("username")
                );
            }

        } catch (Exception e) {
            System.out.println("Error loading admins.");
            e.printStackTrace();
        }
    }

    public void manageBookings(Scanner sc) {

        // STEP 1: SHOW BOOKINGS
        System.out.println("\n=================================");
        System.out.println("        CURRENT BOOKINGS");
        System.out.println("=================================");

        String sql = "SELECT * FROM tickets";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                        rs.getString("ticket_code") + " | " +
                        rs.getString("visitor_name") + " | " +
                        rs.getString("status")
                );
            }

        } catch (Exception e) {
            System.out.println("Error loading bookings.");
            e.printStackTrace();
            return;
        }

        System.out.print("\nEnter Booking ID to manage (0 to exit): ");
        int id = sc.nextInt();
        sc.nextLine();

        if (id == 0) {
            return;
        }

        System.out.println("\n--- ACTIONS ---");
        System.out.println("1. Confirm Booking");
        System.out.println("2. Cancel Booking");
        System.out.println("3. Remove Booking (only CANCELLED)");
        System.out.print("Choose: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {

            case 1:
                updateBookingStatus(id, "CONFIRMED");
                break;

            case 2:
                updateBookingStatus(id, "CANCELLED");
                break;

            case 3:
                deleteIfCancelled(id);
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    public void updateBookingStatus(int id, String status) {
        String sql = "UPDATE tickets SET status=? WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Booking updated to " + status);
            } else {
                System.out.println("Booking not found.");
            }

        } catch (Exception e) {
            System.out.println("Update failed.");
            e.printStackTrace();
        }
    }

    public void deleteIfCancelled(int id) {

        String checkSql = "SELECT status FROM tickets WHERE id=?";
        String deleteSql = "DELETE FROM tickets WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {

                String status = rs.getString("status");

                if (!status.equals("CANCELLED")) {
                    System.out.println("Only CANCELLED bookings can be deleted.");
                    return;
                }

            } else {
                System.out.println("Booking not found.");
                return;
            }

            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, id);
                deletePs.executeUpdate();
                System.out.println("Cancelled booking removed.");
            }

        } catch (Exception e) {
            System.out.println("Delete failed.");
            e.printStackTrace();
        }
    }
}