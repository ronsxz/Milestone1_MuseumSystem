package src.service;

import src.db.DBConnection;
import java.sql.*;

public class TicketService {

    public String bookTicket(String visitorName) {

        String ticketCode = "TCK-" + System.currentTimeMillis();
        String sql = "INSERT INTO tickets(ticket_code, visitor_name, status) VALUES(?, ?, 'PENDING')";

        Connection conn = DBConnection.connect();

        if (conn == null) {
            System.out.println("No DB connection.");
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticketCode);
            ps.setString(2, visitorName);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Ticket booked successfully!");
                System.out.println("Ticket ID: " + ticketCode);
                return ticketCode;
            } else {
                System.out.println("Insert failed (0 rows affected).");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Ticket booking failed:");
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateTicket(String code) {

        String sql = "SELECT * FROM tickets WHERE ticket_code=? AND is_valid=1";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}