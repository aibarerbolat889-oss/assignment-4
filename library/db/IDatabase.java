package library.db;

import java.sql.Connection;

public interface IDatabase {   //to be able to easily change the db
    Connection getConnection();  //declares a method that returns a db connection
}
