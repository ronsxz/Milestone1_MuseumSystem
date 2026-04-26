package src.service;

import src.db.DBConnection;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class ExhibitService {

    public boolean virtualGallery(Scanner sc) {
        String sql = "SELECT * FROM exhibits";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Pag-imbak ng lahat ng data mula sa database
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> descs = new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();
            ArrayList<String> artists = new ArrayList<>();
            ArrayList<String> years = new ArrayList<>();
            ArrayList<String> mediums = new ArrayList<>();
            ArrayList<String> styles = new ArrayList<>();
            ArrayList<String> origins = new ArrayList<>();
            ArrayList<String> significances = new ArrayList<>();
            ArrayList<String> detailed_infos = new ArrayList<>();

            while (rs.next()) {
                ids.add(rs.getInt("id"));
                titles.add(rs.getString("title"));
                descs.add(rs.getString("description"));
                images.add(rs.getString("image"));
                artists.add(rs.getString("artist"));
                years.add(rs.getString("year_created"));
                mediums.add(rs.getString("medium"));
                styles.add(rs.getString("style"));
                origins.add(rs.getString("origin"));
                significances.add(rs.getString("significance"));
                detailed_infos.add(rs.getString("detailed_info"));
            }

            while (true) {
                System.out.println("\n============================================");
                System.out.println("            NATIONAL MUSEUM GALLERY");
                System.out.println("============================================");
                for (int i = 0; i < titles.size(); i++) {
                    System.out.println((i + 1) + ". " + titles.get(i));
                }
                System.out.print("\nSelect exhibit (or Q to go back): ");
                String input = sc.nextLine().trim();

                if (input.equalsIgnoreCase("Q")) {
                    return true; 
                }

                int index;
                try {
                    index = Integer.parseInt(input) - 1;
                    if (index < 0 || index >= titles.size()) {
                        System.out.println("Invalid selection. Please try again.");
                        continue;
                    }

                    while (true) {
                        // --- BASIC INFORMATION (Laging lumalabas) ---
                        System.out.println("\n--------------------------------------------");
                        System.out.println("ID: " + ids.get(index));
                        System.out.println("Title: " + titles.get(index));
                        System.out.println("Description: " + descs.get(index));
                        System.out.println("Image File: " + images.get(index));
                        System.out.println("--------------------------------------------");

                        // --- USER INPUT PARA SA ADDITIONAL DETAILS ---
                        System.out.print("\nView additional detailed info? (Y/N): ");
                        String showMore = sc.nextLine().trim();

                        if (showMore.equalsIgnoreCase("Y")) {
                            System.out.println("\n>>> DETAILED INFORMATION <<<");
                            System.out.println("Artist:       " + (detailed_infos.get(index) != null ? detailed_infos.get(index) : "N/A"));
                            System.out.println("Artist:       " + (artists.get(index) != null ? artists.get(index) : "N/A"));
                            System.out.println("Year Created: " + (years.get(index) != null ? years.get(index) : "N/A"));
                            System.out.println("Medium:       " + (mediums.get(index) != null ? mediums.get(index) : "N/A"));
                            System.out.println("Style:        " + (styles.get(index) != null ? styles.get(index) : "N/A"));
                            System.out.println("Origin:       " + (origins.get(index) != null ? origins.get(index) : "N/A"));
                            System.out.println("Significance: " + (significances.get(index) != null ? significances.get(index) : "N/A"));
                            System.out.println("--------------------------------------------");
                        }

                        // --- PAGPILI ULIT O PAG-EXIT ---
                        System.out.print("\nEnter index to view another or 'Q' for main menu: ");
                        String action = sc.nextLine().trim();

                        if (action.equalsIgnoreCase("Q")) {
                            return true;
                        }

                        try {
                            int newIndex = Integer.parseInt(action) - 1;
                            if (newIndex >= 0 && newIndex < titles.size()) {
                                index = newIndex; // I-update ang index para sa susunod na loop
                            } else {
                                System.out.println("Invalid exhibit number.");
                                break; // Babalik sa main list
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Returning to exhibit list...");
                            break; // Babalik sa main list
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or Q.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }
}