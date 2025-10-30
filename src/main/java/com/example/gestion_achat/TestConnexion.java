package com.example.gestion_achat;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnexion {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/gestion_achats";
        String user = "postgres";
        String password = "bal12345";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}