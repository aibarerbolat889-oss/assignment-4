package library.db;

import edu.aitu.oop3.db.DatabaseConnection;
import java.sql.Connection;

public class PostgresDatabase implements IDatabase { //class implements the interface,responsible for PostgreSQL.

    @Override
    public Connection getConnection() {
        try {
            return DatabaseConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
