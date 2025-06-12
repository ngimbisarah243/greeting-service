package org.repository;

import org.model.Greeting;

import java.sql.*;

public class GreetingRepository {
    private final String url;
    private final String user;
    private final String password;

    public GreetingRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Greeting findById(int id) {
        String sql = "SELECT id, greeting_text FROM greeting WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Greeting(
                        rs.getInt("id"),
                        rs.getString("greeting_text")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
