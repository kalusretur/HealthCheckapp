package itms.com.pe.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConexionTeleconsulta {
	public static Connection getConexion(){
		
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://192.168.3.240/teleasistencia";
			String usr = "opitms";
			String psw = "Morris74rh+o";
			/*String url = "jdbc:mysql://192.168.3.55/itmscomp_digitalizacion";
			String usr = "root";
			String psw = "itms.1110";*/
			con = DriverManager.getConnection(url,usr,psw);
		} catch (ClassNotFoundException ex) {
			System.out.println("Error >> Driver no Instalado!!");
		} catch (SQLException ex) {
			System.out.println("Error >> de conexión con la BD");
		}
		
		return con;
	}
}
