package itms.com.pe.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConexion {
	
	public static Connection getConexion(){
		Connection con = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			String dbURL = "jdbc:oracle:thin:@192.168.3.232:1521:PIT";
			String username = "pit_admin";
			String password = "pit";
			con = DriverManager.getConnection(dbURL, username, password);
			//con=DriverManager.getConnection("jdbc:oracle:thin:@192.168.3.232:1521:xe","pit_exa","pit");  
			
		} catch (ClassNotFoundException ex) {
			System.out.println("Error >> Driver no Instalado!!");
		} catch (SQLException ex) {
			System.out.println("Error >> de conexión con la BD");
		}
		
		return con;
	}

}
