package itms.com.pe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import itms.com.pe.beans.Ap_Usuario;
import itms.com.pe.beans.AtencionesBE;
import itms.com.pe.beans.CitasBE;
import itms.com.pe.beans.CostoServicioBE;
import itms.com.pe.beans.DomicilabBE;
import itms.com.pe.beans.EspecialidadTABE;
import itms.com.pe.beans.KeyTokenBE;
import itms.com.pe.beans.MailCitaBE;
import itms.com.pe.beans.PacientesBE;
import itms.com.pe.beans.PacientesMedBE;
import itms.com.pe.beans.RQdomicilabBE;
import itms.com.pe.beans.TeleasistenciaBE;
import itms.com.pe.beans.TeleasistenciaProcesoBE;
import itms.com.pe.beans.TeleasistenciaVideoBE;
import itms.com.pe.beans.Teleconsulta_ID;
import itms.com.pe.beans.UsuarioLoginBE;
import itms.com.pe.beans.ValidaEmail;
import itms.com.pe.beans.VersionAppBE;
import itms.com.pe.beans.culqiBE;
import itms.com.pe.conection.MySQLConexion;
import itms.com.pe.conection.MySQLConexionAll;
import itms.com.pe.conection.MySQLConexionTeleconsulta;
import itms.com.pe.conection.OracleConexion;
import itms.com.pe.utils.*;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	Encryptor encryptor = new Encryptor();
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	

	
	/*----------------------------------------------------*/
	
	/*-----METODOS GET ---- ITMS*/
	@RequestMapping(value="/validarDatos", method = RequestMethod.GET)
	public @ResponseBody ValidaEmail validarEmail(@RequestParam(value="email", required=false)String email,
												  @RequestParam(value="imei", required=false)String imei) throws SQLException{
		Connection con = null;
		ValidaEmail ve = new ValidaEmail();
		 
		 try {
			 con =  MySQLConexion.getConexion();
			 Statement consult = con.createStatement();
			 String sql = "SELECT USR_CORREO, USR_PHONE, USR_IMEI FROM AP_USUARIOS WHERE USR_CORREO = '"+email+"' or USR_IMEI='"+imei+"'" ;
			 //String sql = "SELECT USU_USUARIO, USU_CLAVE, USU_ACTIVO FROM TM_USUARIO WHERE USU_USUARIO='"+name+"' and USU_CLAVE='"+pass+"' and USU_ACTIVO=1";
			 ResultSet resul = consult.executeQuery(sql);
			 if(resul.next()){
				 ve.setUSR_CORREO(resul.getString(1));
				 ve.setUSR_PHONE(resul.getString(2));
				 ve.setUSR_IMEI(resul.getString(3));
				 return ve;
			 }
			 else
				 return ve;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ve;
		}finally{
			if (con!= null) con.close();
		}
	}
	
	
	@RequestMapping(value="/validarLoginAPP", method = RequestMethod.POST)
	public @ResponseBody Ap_Usuario validaLoginAPP (@RequestParam(value="email", required=true) String email,
													@RequestParam(value="passwd", required=true) String passwd) throws SQLException{
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Ap_Usuario ap_usr = new Ap_Usuario();
		
		try {
			con = MySQLConexion.getConexion();
			stmt = con.createStatement();
			//String sql =  "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02 from AP_USUARIOS U LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR WHERE U.USR_CORREO='"+email+"' AND U.USR_CLAVE='"+passwd+"'";
			//String sql = "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02, UPPER(CONCAT(PA.PACI_NOM,' ',PA.PACI_APE_PAT, ' ', PA.PACI_APE_MAT)) AS PACIENTE from AP_USUARIOS U JOIN AP_PACIENTES PA ON PA.COD_USR=U.COD_USR LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR WHERE PA.PACI_PARENTESCO=0 AND U.USR_CORREO='"+email+"' AND U.USR_CLAVE='"+passwd+"' LIMIT 1;";
			/*String sql = "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02, "
					+ "UPPER(CONCAT(PA.PACI_NOM,' ',PA.PACI_APE_PAT, ' ', PA.PACI_APE_MAT)) AS PACIENTE from AP_USUARIOS U  "
					+ " LEFT JOIN AP_PACIENTES PA ON PA.COD_USR=U.COD_USR LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR "
					+ "WHERE PA.PACI_PARENTESCO IN (0) AND U.USR_CORREO='"+email+"'  AND U.USR_CLAVE='"+passwd+"' UNION "
					+ "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02,  U.USR_CORREO "
					+ "from AP_USUARIOS U LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR "
					+ "WHERE U.USR_CORREO='"+email+"' AND U.USR_CLAVE='"+passwd+"'"
					+ "LIMIT 1";*/
			String sql = "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02, "
					+ "UPPER(CONCAT(PA.PACI_NOM,' ',PA.PACI_APE_PAT, ' ', PA.PACI_APE_MAT)) AS PACIENTE ,D.COD_06 from AP_USUARIOS U  "
					+ " LEFT JOIN AP_PACIENTES PA ON PA.COD_USR=U.COD_USR LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR "
					+ "WHERE PA.PACI_PARENTESCO IN (0) AND U.USR_CORREO='"+email+"'  AND U.USR_CLAVE='"+passwd+"' UNION "
					+ "SELECT U.COD_USR, U.USR_CORREO, U.USR_PHONE, U.USR_IMEI, U.USR_ACTIVO, D.COD_02,  U.USR_CORREO, D.COD_06 "
					+ "from AP_USUARIOS U LEFT JOIN AP_MUL_DET D ON D.COD_01 = U.COD_USR "
					+ "WHERE U.USR_CORREO='"+email+"' AND U.USR_CLAVE='"+passwd+"'"
					+ "LIMIT 1";
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				ap_usr.setCOD_USR(rs.getInt(1));
				ap_usr.setUSR_CORREO(rs.getString(2));
				ap_usr.setUSR_PHONE(rs.getString(3));
				ap_usr.setUSR_IMEI(rs.getString(4));
				ap_usr.setUSR_ACTIVO(rs.getInt(5));
				ap_usr.setUSR_ROL(rs.getInt(6));
				ap_usr.setUSR_TITULAR(rs.getString(7));
				ap_usr.setCLIE_ID(rs.getString(8));
			}
			else
				return ap_usr;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if (con!= null) con.close();
		}
		
		return ap_usr;
	}
	
	
	
	@RequestMapping(value="/activarCuenta", method = RequestMethod.GET)
	public @ResponseBody String  ActivarCuenta(@RequestParam(value="email", required=true) String email, HttpServletResponse response)  throws SQLException{
		
		Connection con = null;
		PreparedStatement pst = null;
		
		 
		 try {
			 /*Print Encrip*/
			 String key = "21AE50A80ITMS1O4"; //llave
			 String iv = "1957533836ITMSAB";
			 String valEncrip = email.replace(" ", "+");
			 String valDescp = encryptor.decrypt(key, iv, valEncrip);
			 System.out.println(valDescp);
			 con =  MySQLConexion.getConexion();
			 String sql = "UPDATE AP_USUARIOS SET USR_FEC_ACTIVO=SYSDATE(), USR_ACTIVO=1 WHERE USR_CORREO='"+valDescp+"'";
			 pst = con.prepareStatement(sql);
			 pst.executeUpdate();
			 
			 
			 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}finally{
			if (con!= null) con.close();
		}
		 response.setContentType("text/html");
		 String web = "<center><div ><table ><tr><td width='400px'><h0>TU CUENTA HA SIDO ACTIVADA Y VERIFICADA SATISFACTORIAMENTE</h0></td><td><img src='http://www.pitperu.com.pe/app/itmslogo2.png' alt='Cheetah!'</td></tr><tr></tr></table></div></center>";
		 return web;
		
	}
	
	@RequestMapping(value="/validarExistePaci", method = RequestMethod.GET)
	public @ResponseBody int ValidaExiste(@RequestParam(value="cod_p", required=true) String codigo) throws SQLException{
		
		Connection con = null;
		int rs = 0;
		
		try {
			con = MySQLConexion.getConexion();
			Statement consult = con.createStatement();
			String sql = "SELECT COUNT(*) from AP_PACIENTES P INNER JOIN AP_USUARIOS U ON U.COD_USR = P.COD_USR WHERE P.COD_USR ="+codigo+" AND PACI_ACTIVO=1" ;
			 //String sql = "SELECT USU_USUARIO, USU_CLAVE, USU_ACTIVO FROM TM_USUARIO WHERE USU_USUARIO='"+name+"' and USU_CLAVE='"+pass+"' and USU_ACTIVO=1";
			 ResultSet resul = consult.executeQuery(sql);
			 resul.next();
			 rs = resul.getInt(1);
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if (con!= null) con.close();
		}
		
		
		return rs;
	}
	
	@RequestMapping(value="/listaPacientes", method = RequestMethod.GET)
	public @ResponseBody ArrayList<PacientesBE> listadoPacientes(@RequestParam(value="cod_usr", required=true) int cod_usr) throws SQLException{
		ArrayList<PacientesBE> lista= new ArrayList<PacientesBE>();
		Connection con = null;
		
		try {
			con = MySQLConexion.getConexion();
			Statement consult = con.createStatement();
			String sql = "SELECT P.PACI_ID, P.COD_USR, CONCAT(P.PACI_NOM,' ',P.PACI_APE_PAT, ' ',P.PACI_APE_MAT) AS PACIENTE, P.PACI_PHONE FROM AP_PACIENTES P JOIN AP_USUARIOS U ON U.COD_USR = P.COD_USR WHERE P.COD_USR="+cod_usr+" AND P.PACI_ACTIVO=1";
			ResultSet resul = consult.executeQuery(sql);
			while(resul.next()){
				PacientesBE objPaci = new PacientesBE();
				objPaci.setPACI_ID(resul.getInt(1));
				objPaci.setCOD_USR(resul.getInt(2));
				objPaci.setPACIENTE(resul.getString(3));
				objPaci.setPACI_PHONE(resul.getString(4));
				lista.add(objPaci);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if (con!= null) con.close();
		}
		
		return lista;
	}
	
	@RequestMapping(value="/listPrecioServicios", method = RequestMethod.GET)
	public @ResponseBody ArrayList<CostoServicioBE> listadoPrecioServicios(@RequestParam(value="val_table", required=true) int val_table) throws SQLException{
		ArrayList<CostoServicioBE> lista= new ArrayList<CostoServicioBE>();
		Connection con = null;
		
		try {
			con = MySQLConexion.getConexion();
			Statement consult = con.createStatement();
			String sql = "SELECT DES_NOMBRE, COD_01, COD_05 FROM AP_MUL_DET where ID_TABLA="+val_table+" ORDER BY ID_DETALLE";
			ResultSet resul = consult.executeQuery(sql);
			while(resul.next()){
				CostoServicioBE objCosto = new CostoServicioBE();
				objCosto.setNombre(resul.getString(1));
				objCosto.setTipo_exa(resul.getInt(2));
				objCosto.setPrecio(resul.getDouble(3));
				lista.add(objCosto);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if (con!= null) con.close();
		}
		
		return lista;
	}
	
	
/*-----METODOS DO POST--- ITMS*/
	

	
	@RequestMapping(value="/registroAccount", method=RequestMethod.POST)
	public ResponseEntity<?> createAccount(@RequestParam(value="mail", required=true)String mail,
									@RequestParam(value="password", required=true)String password,
									@RequestParam(value="phone", required=true)String phone,
									@RequestParam(value="imei", required=true)String imei){
		
		Connection con = null;
		PreparedStatement pst = null;
		int rs = 0;
		
		try {
			con = MySQLConexion.getConexion();
			//String sql = "INSERT INTO PRUEBA VALUE (?)";
			String sql = "INSERT INTO AP_USUARIOS VALUES (default, ?, ?, ?, ?, default, default, default)";
			pst = con.prepareStatement(sql);
			pst.setString(1, mail);
			pst.setString(2, password);
			pst.setString(3, phone);
			pst.setString(4, imei);
			
			rs = pst.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en la sentencia: " + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (pst!= null) pst.close();
				if (con!= null) con.close();
			} catch (Exception e) {
				System.out.println("Error al cerrar: " + e.getMessage());
				e.printStackTrace();
			}
		}
		if(rs==1){
			return new ResponseEntity<String>(HttpStatus.CREATED);
		}else{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/insertPaci", method=RequestMethod.POST)
	public ResponseEntity<UsuarioLoginBE> insertPaci(@RequestParam(value="cod_usr", required=true)int cod_usr,
										@RequestParam(value="paci_parentesco", required=true)int paci_parentesco,
										@RequestParam(value="paci_genero", required=true)String paci_genero,
										@RequestParam(value="paci_nom", required=true)String paci_nom,
										@RequestParam(value="paci_ape_pat", required=true)String paci_ape_pat,
										@RequestParam(value="paci_ape_mat", required=true)String paci_ape_mat,
										@RequestParam(value="paci_phone", required=true)String paci_phone,
										@RequestParam(value="paci_fec_nac", required=true)String paci_fec_nac,
										@RequestParam(value="paci_paci_tip_doc", required=true)int paci_paci_tip_doc,
										@RequestParam(value="paci_rut", required=true)String paci_rut,
										@RequestParam(value="paci_site", required=true)String paci_site,
										@RequestParam(value="paci_city", required=true)String paci_city,
										@RequestParam(value="paci_activo", required=true)int paci_activo){
		UsuarioLoginBE usr = new UsuarioLoginBE();
		Connection con = null;
		PreparedStatement pst = null;
		int rs = 0;
		
		try {
			con = MySQLConexion.getConexion();
			String sql = "INSERT INTO AP_PACIENTES VALUES (default,?,?,?,?,?,?,?,?,?,?,?,?,?,default)";
			pst = con.prepareStatement(sql);
			pst.setInt(1, cod_usr);
			pst.setInt(2, paci_parentesco);
			pst.setString(3, paci_genero);
			pst.setString(4, paci_nom);
			pst.setString(5, paci_ape_pat);
			pst.setString(6, paci_ape_mat);
			pst.setString(7, paci_phone);
			pst.setString(8, paci_fec_nac);
			pst.setInt(9, paci_paci_tip_doc);
			pst.setString(10, paci_rut);
			pst.setString(11, paci_site);
			pst.setString(12, paci_city);
			pst.setInt(13, paci_activo);
			
			rs = pst.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en la sentencia: " + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (pst!= null) pst.close();
				if (con!= null) con.close();
			} catch (Exception e) {
				System.out.println("Error al cerrar: " + e.getMessage());
				e.printStackTrace();
			}
		}
		if(rs==1){
			usr.setCod_usr(cod_usr);
			String paciente = paci_nom + " " + paci_ape_pat + " " + paci_ape_mat;
			usr.setPaciente_login(paciente);
			return new ResponseEntity<UsuarioLoginBE>(usr, HttpStatus.CREATED);
		}else
		return new ResponseEntity<UsuarioLoginBE>(usr, HttpStatus.BAD_REQUEST);
	}
	
	
	@RequestMapping(value="/insertCitaDB", method=RequestMethod.POST)
	public ResponseEntity<MailCitaBE> insertCitaDB(@RequestParam(value="codigo", required=true)int codigo,
										  @RequestParam(value="paci", required=true)int paci,
										  @RequestParam(value="tipproc", required=true)int tipproc,
										  @RequestParam(value="tippag", required=true)int tippag,
										  @RequestParam(value="montoproc", required=true)String montoproc,										  
										  @RequestParam(value="marcapaso", required=false)int marcapaso,
										  @RequestParam(value="distrito", required=true)String distrito,
										  @RequestParam(value="direccion", required=true)String direccion,										  
										  @RequestParam(value="latitud", required=true)String latitud,
										  @RequestParam(value="longitud", required=true)String longitud,
										  @RequestParam(value="estpago", required=false)int estpago,
										  @RequestParam(value="codpago", required=false)String codpago,
										  @RequestParam(value="fecpago", required=false)String fecpago,
										  @RequestParam(value="espta", required=false)String espta,
										  @RequestParam(value="estado_cita", required=true)int estado_cita){
		
		Connection con = null;
		PreparedStatement pst = null;
		int rs = 0;
		ResultSet resul= null;
		MailCitaBE ci = new MailCitaBE();
		Statement consult = null;
		
		try {
			con = MySQLConexion.getConexion();
			//String sql = "INSERT INTO PRUEBA VALUE (?)";
			String sql = "INSERT INTO AP_CITAS (COD_USR,PACI_ID,TIPO_PROC,TIPO_PAGO,MONTO_PROC,MARCA_PASO,CITA_DISTRITO,CITA_DIRECCION,CITA_CORD_LATITUD,CITA_CORD_LONGITUD,EST_PAGO,COD_PAGO,FEC_PAGO,ID_ESPECIALIDAD_TA,CITA_ESTADO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String sql2 = "SELECT C.CITA_ID, C.MONTO_PROC, UPPER(CONCAT(PA.PACI_NOM,' ',PA.PACI_APE_PAT, ' ', PA.PACI_APE_MAT)) AS PACIENTE, PROC.DES_NOMBRE, C.CITA_DISTRITO, C.CITA_DIRECCION, DATE_FORMAT(C.CITA_FECHA,'%d/%m/%Y %H:%i') AS FECHA, TPAGO.DES_NOMBRE AS TIPOPAGO, ESPAGO.DES_NOMBRE AS ESTADOPAGO, C.TIPO_PROC FROM AP_CITAS C JOIN AP_PACIENTES PA ON PA.PACI_ID = C.PACI_ID JOIN AP_MUL_DET PROC ON PROC.COD_01 = C.TIPO_PROC AND PROC.ID_TABLA=2 JOIN AP_MUL_DET TPAGO ON TPAGO.COD_01 = C.TIPO_PAGO AND TPAGO.ID_TABLA=5 JOIN AP_MUL_DET ESPAGO ON ESPAGO.COD_01 = C.EST_PAGO AND ESPAGO.ID_TABLA=6 WHERE C.CITA_ID IN (SELECT LAST_INSERT_ID())";
			//String sql2 = "SELECT LAST_INSERT_ID()";
			consult = con.createStatement();
			pst = con.prepareStatement(sql);
			pst.setInt(1, codigo);
			pst.setInt(2, paci);
			pst.setInt(3, tipproc);
			pst.setInt(4, tippag);
			pst.setString(5, montoproc);
			pst.setDouble(6, marcapaso);
			pst.setString(7, distrito);
			pst.setString(8, direccion);
			pst.setString(9, latitud);
			pst.setString(10, longitud);
			pst.setInt(11, estpago);
			pst.setString(12, codpago);
			pst.setString(13, fecpago);
			pst.setString(14, espta);
			pst.setInt(15, estado_cita);
			//System.out.println(sql);
			
			rs = pst.executeUpdate();
			resul = consult.executeQuery(sql2);
			resul.next();			
			ci.setCita_id(resul.getInt(1));
			ci.setMonto_proc(resul.getString(2));
			ci.setPaciente(resul.getString(3));
			ci.setProcedimiento(resul.getString(4));
			ci.setDistrito(resul.getString(5));
			ci.setDireccion(resul.getString(6));
			ci.setFecha(resul.getString(7));
			ci.setTipopago(resul.getString(8));			
			ci.setEstadopago(resul.getString(9));
			ci.setCod_proc(resul.getInt(10));
			//System.out.println(resul.getInt(1));
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error en la sentencia: " + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (pst!= null) pst.close();
				if (con!= null) con.close();
				if (resul!= null) resul.close();
				if(consult!=null) consult.close();
			} catch (Exception e) {
				System.out.println("Error al cerrar: " + e.getMessage());
				e.printStackTrace();
			}
		}
		if(rs==1){
			return new ResponseEntity<MailCitaBE>(ci, HttpStatus.CREATED);
		}else		
		return new ResponseEntity<MailCitaBE>(HttpStatus.BAD_REQUEST);
	}
	
	/*Metodo Culqui*/
	/*
	@RequestMapping(value="/prubaCulqui", method=RequestMethod.POST)
	public @ResponseStatus(value = HttpStatus.BAD_REQUEST)  int repote(){
		int HttpStatusCodeException;
		return HttpStatusCodeException=202;
		
	}*/
	
	@RequestMapping(value="/obtenerKEY", method=RequestMethod.POST, headers = {"Autorizacion=ITMSperu"})
	public @ResponseBody KeyTokenBE obtenerKeyAPP(@RequestParam(value="key", required=true) int key){
		KeyTokenBE be = new KeyTokenBE();
		if(key==10){
			be.setPk_public("pk_live_9qgSU17Itq6boXu4");
			be.setPk_private("sk_live_lzZEkEZ9YutKn4m9");
			/*
			be.setPk_public("pk_test_yadk4CgGUfhMdRyb");
			be.setPk_private("sk_test_W0A0k33Bma3WIfqS");*/
			return be;
		}
		else
			return be;
		
	}
	
	@RequestMapping(value="/generarCargo", method=RequestMethod.POST)
	public ResponseEntity<?> fectPrueba(@RequestParam(value="id", required=true)int id){
		try {
			if(id==1){
				KeyTokenBE be = new KeyTokenBE();
				be.setPk_public("pk_test_yadk4CgGUfhMdRyb");
				be.setPk_private("sk_test_W0A0k33Bma3WIfqS");
				return new ResponseEntity<KeyTokenBE>(HttpStatus.CREATED);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
		
	}
	@RequestMapping(value="/pruebaHTTP", method=RequestMethod.GET)
	public ResponseEntity<String> xxx(){
		return new ResponseEntity<String>(HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/pruebaCall", method = RequestMethod.GET)
	public @ResponseBody String getget(){
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(
		        "http://domicilab.com.pe:88/Service.svc?wsdl",
		        String.class);

		System.out.println(response);
		System.out.println(response.getStatusCode());
		return "Ok";
	}
	
	@RequestMapping(value="/pruebita", method = RequestMethod.GET)
	public ResponseEntity<String> getget50(){
		HttpStatus code = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(
			        "http://localhost:8080/pe/pruebaHTTP",
			        String.class);
			code = response.getStatusCode();
			System.out.println(response);
			System.out.println(response.getStatusCode());
		} catch (final HttpClientErrorException e) {
			// TODO: handle exception
			code=e.getStatusCode();
			System.out.println("CODIGO : "+e.getStatusCode());
			//e.printStackTrace();
		}
		
		return new ResponseEntity<String>(code);
	}
	
	/**--------------------------------**/
	@RequestMapping(value="/obtenerKEY2", method=RequestMethod.POST, headers = {"Autorizacion=ITMSperu"})
	public @ResponseBody KeyTokenBE obtenerKeyAPP222(){
		KeyTokenBE be = new KeyTokenBE();
		
			be.setPk_public("pk_live_9qgSU17Itq6boXu4");
			be.setPk_private("sk_live_lzZEkEZ9YutKn4m9");
			return be;
		
		
	}
	
	@RequestMapping(value="/pruebaCall20BK", method = RequestMethod.GET)
	public @ResponseBody String getge20tBK() throws JSONException{
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.add("Authorization", "Bearer sk_live_lzZEkEZ9YutKn4m9");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		JSONObject json = new JSONObject();
		json.put("amount", "100");
		json.put("currency_code", "PEN");
		json.put("email", "soporte@itms.com.pe");
		json.put("source_id", "tkn_live_hkUi1D54WkZQpHrf");
		HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange("https://api.culqi.com/v2/charges", HttpMethod.POST, entity, String.class);
		System.out.println(response);
		System.out.println(response.getStatusCode());
		return "Ok";
	}
	
	
	@RequestMapping(value="/pruebaCall20_bkSINCODERESPONSE", method = RequestMethod.POST)
	public @ResponseBody String getge20tSINREPONSE(@RequestParam(value="charge_amount", required=true) int charge_amount,
										 @RequestParam(value="mail", required=true) String mail,
										 @RequestParam(value="token_id", required=true) String token_id) throws JSONException{
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.add("Authorization", "Bearer sk_live_lzZEkEZ9YutKn4m9");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		JSONObject json = new JSONObject();
		json.put("amount", charge_amount);
		json.put("currency_code", "PEN");
		json.put("email", mail);
		json.put("source_id", token_id);
		HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange("https://api.culqi.com/v2/charges", HttpMethod.POST, entity, String.class);
		System.out.println(response);
		System.out.println(response.getStatusCode());
		return "Ok";
	}
	
	@RequestMapping(value="/pruebaCall20", method = RequestMethod.POST)
	public @ResponseBody String getge20t(@RequestParam(value="charge_amount", required=true) int charge_amount,
										 @RequestParam(value="mail", required=true) String mail,
										 @RequestParam(value="token_id", required=true) String token_id) throws JSONException{
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.add("Authorization", "Bearer sk_live_lzZEkEZ9YutKn4m9");
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		JSONObject json = new JSONObject();
		json.put("amount", charge_amount);
		json.put("currency_code", "PEN");
		json.put("email", mail);
		json.put("source_id", token_id);
		HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange("https://api.culqi.com/v2/charges", HttpMethod.POST, entity, String.class);
		System.out.println(response);
		System.out.println(response.getStatusCode());
		return "Ok";
	}
	
	
	
	@RequestMapping(value="/pruebaMail",method = RequestMethod.POST)
		public @ResponseBody void sendmail(@RequestParam(value="key", required=true) int key) throws Exception{
		// SMTP server information
		String host = "smtp.gmail.com";
		String port = "587";
		String mailFrom = "operaciones@itms.com.pe";
		String password = "itms.1110#";

		// outgoing message information
		String mailTo = "soporte@itms.com.pe";
		String subject = "Bienvenido al Servicio de Telemedicina";
		//Variables
		String body = "Gracias por registrate en nuestra aplicación de servicios médicos a domicilio. Con ella podrás solicitar los diversos servicios que brindamos y seras atendido el mismo día que programes tu cita.";
		String httpd = "http://201.234.48.180:8080/MiServicioREST/activarCuenta?email=$link";
				
		String message = "<table border='0' width='566' style='font-size: 15px'>"
				+ "<tr><td width='100%'><img style='display:block;margin:0 auto 0 auto;' src='cabezera.png'></td></tr>"
				+ "<tr><td width='100%'><hr style='background-color: #0080ff; height: 3px;' /></td></tr>"
				+ "<tr><td><p></p></td><tr>"
				+ "<tr><td><p align='justify'>Bienvenido a Telemedicina de Perú</p></td><tr>"
				+ "<tr><td><p align='justify'>"+body+"</p></td><tr>"
				+ "<tr><td><br><br></td></tr>"
				+ "<tr><td><a href="+httpd+"><img style='display:block;margin:0 auto 0 auto;' src='button.png'></a></td></tr>"
				+ "<tr><td><br><br></td><tr>"
				+ "<tr><td>Si no puede ver la imagen haga clic <a href="+httpd+">aqui</a></td></tr>"
				+ "</table>";
		
		HtmlEmailSender mailer = new HtmlEmailSender();
		
		try {
			mailer.sendHtmlEmail(host, port, mailFrom, password, mailTo,
					subject, message);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Failed to sent email.");
			ex.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="/citaMail",method = RequestMethod.POST)
	public @ResponseBody void sendmailpostCita(@RequestParam(value="paciente", required=true) String paciente,
											   @RequestParam(value="service", required=true) String service,
											   @RequestParam(value="mont_proc", required=true) String mont_proc,
											   @RequestParam(value="distrito", required=true) String distrito,
											   @RequestParam(value="direccion", required=true) String direccion,
											   @RequestParam(value="fecha", required=true) String fecha,
											   @RequestParam(value="tippago", required=true) String tippago,
											   @RequestParam(value="estpago", required=true) String estpago,
											   @RequestParam(value="cita_id", required=true) int cita_id,
											   @RequestParam(value="mail_paci", required=true) String mail_paci) throws Exception{
	// SMTP server information
	String host = "smtp.gmail.com";
	String port = "587";
	String mailFrom = "operaciones@itms.com.pe";
	String password = "itms.1110#";

	// outgoing message information
	String mailTo = "serviciosapp@itms.com.pe";
	String subject = "Solicitud de Servicio APP- CheckHealth";
	//Variables
			
	// message contains HTML markups
			String message = "<?php ?>"
					+ "<html>	<body><table border='0' width='566' style='font-size: 15px'>"
					+ "<tr><td  width='100%' colspan='2'><img style='display:block;margin:0 auto 0 auto;' src='http://www.pitperu.com.pe/app/cabezera2.png'></td></tr>"
					+ "<tr><td width='100%' colspan='2'><hr style='background-color: #0080ff; height: 3px;' /></td></tr>"
					+ "<tr><td colspan='2'>Estimados,</td></tr>"
					+ "<tr><td colspan='2'>Se informa el agendamiento del siguiente paciente :</td></tr>"
					+ "<tr><td><br></td></tr>"
					+ "<tr><td style='height: 4px'>Paciente : </td>	<td style='height: 4px'><b>"+paciente+"</b></td>	</tr>"
					+ "<tr><td>Teléfono :</td><td><b>"+mail_paci+"</b></td></tr>"
					+ "<tr><td>Procedimiento :</td><td><b>"+service+"</b></td></tr>"
					+ "<tr><td>Costo Procedimiento :</td><td><b><span style='background-color:rgb(255,255,0)'>"+mont_proc+"</span></b></td></tr>"
					+ "<tr><td>Distrito :</td><td>"+distrito+"</td></tr>"
					+ "<tr><td>Dirección :</td><td>"+direccion+"</td></tr>"
					+ "<tr><td>Fecha  :</td><td>"+fecha+"</td></tr>"
					+ "<tr><td>Tipo de Pago :</td><td>"+tippago+"</td></tr>"
					+ "<tr><td>Estado de Pago :</td><td><font color=red</font> <b>"+estpago+" </b></td></tr>"
					+ "<tr><td>Número de Cita  :</td><td> <b>"+cita_id+" </b></td></tr>"					
					+ "<tr><td><br><br></td></tr>"
					+ "<tr><td align='center'>Saludos Cordiales<br> <font color=blue>Sistemas ITMS.</font></td></tr>"
					+ "</table></body></html>";
	
		HtmlEmailSender mailer = new HtmlEmailSender();
		
		try {
			mailer.sendHtmlEmail(host, port, mailFrom, password, mailTo,
					subject, message);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Failed to sent email.");
			ex.printStackTrace();
		}
	}
	
	
	/*Envio correo al Cliente*/
	@RequestMapping(value="/citaMailCliente",method = RequestMethod.POST)
	public @ResponseBody void sendmailCliente(@RequestParam(value="mailcliente", required=true) String mailcliente,
											  @RequestParam(value="paciente", required=true) String paciente,
											   @RequestParam(value="service", required=true) String service,
											   @RequestParam(value="mont_proc", required=true) String mont_proc,
											   @RequestParam(value="direccion", required=true) String direccion,
											   @RequestParam(value="fecha", required=true) String fecha,
											   @RequestParam(value="tippago", required=true) String tippago,
											   @RequestParam(value="estpago", required=true) String estpago) throws Exception{
	// SMTP server information
	String host = "smtp.gmail.com";
	String port = "587";
	String mailFrom = "operaciones@itms.com.pe";
	String password = "itms.1110#";

	// outgoing message information
	String mailTo = mailcliente;
	String subject = "Solicitud de Servicio APP- CheckHealth";
	//Variables
			
	// message contains HTML markups
			String message = "<?php ?>"
					+ "<html> <body> <table align='center' border='0' width='600'> <tr><td colspan='2' align='center' style='width:50%;background:#004678; border-radius:10px 10px 0px 0px; ' ><img src='http://www.pitperu.com.pe/app/newlogo2.png' /></td></tr> "
					+ "<tr><td style='padding-left:50px; font-family:Arial'><font size='5'>Estimado(a)&nbsp;:</font></td><td><font size='5'>"+paciente+"</font> </td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td colspan='3' style='text-align:justify; height: 30px'>Se registró el servicio con éxito, en breve nos pondremos en contacto con usted.<br/> Los datos para la atención son los siguientes :</td></tr>"
					+ "<tr ><td><br /></td></tr><tr bgcolor='#F1EFEF'><td style='height: 30px'>Procedimiento :</td><td style='height: 30px'><b>"+service+"</b></td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td style='height: 30px'>Costo Procedimiento :</td><td style='height: 30px'>"+mont_proc+"</td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td style='height: 30px'>Direción  :</td><td style='height: 30px'>"+direccion+"</td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td style='height: 30px'>Fecha :</td><td style='height: 30px'>"+fecha+"</td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td style='height: 30px'>Método de Pago :</td><td style='height: 30px'>"+tippago+"</td></tr>"
					+ "<tr bgcolor='#F1EFEF'><td style='height: 30px'>Estado de Pago :</td><td style='height: 30px'><font color=red</font> <b>"+estpago+"</b></td></tr>"
					+ "<tr><td colspan='3' style='text-align:justify; height: 30px'>Si el método de pago seleccionado es transferencia bancaria, a continuacion te brindamos nuestros números de cuentas, una vez realizado la transferencia responder el correo con el número de operación.</td></tr>"
					+ "<tr><td><b>Banco de Crédito BCP : </b><br/> Cuenta Corriente Soles : 	194-1499409-0-43  	</td><td> <br/>CCI : 	002-194-001499409043-90</td></tr>"
					+ "<tr><td><b>BBVA Continental : </b><br/> Cuenta Corriente Soles : 	0011-0378-0100033956  	</td><td> <br/>CCI : 	011-378-000100033956-72</td></tr>"
					+ "<tr><td><br /></td></tr>"
					+ "<tr style='height: 50px'>"
					+ "<!--<td>© 2017 ITMS - Todos los derechos reservados.</td>-->"
					+ "<td colspan='2' style='width:50%;background:#004678; border-radius:0px 0px 10px 10px; '><b style='color: #FFFFFF; padding-left: 30px; '>© 2017 ITMS - All Rights Reserved</b><a style='padding-left: 250px' href='https://www.facebook.com/Telemedicina-de-Per%C3%BA-2003986163158051/' target='_blank'><img src='http://www.pitperu.com.pe/app/fb.png' /></a></td>"
					+ "</tr>"
					+ "</table>"
					+ "</body>"
					+ "</html>";
	
		HtmlEmailSender mailer = new HtmlEmailSender();
		
		try {
			mailer.sendHtmlEmail(host, port, mailFrom, password, mailTo,
					subject, message);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Failed to sent email.");
			ex.printStackTrace();
		}
	}
	
	
	/**-------------------**/
	@RequestMapping(value="/persona", method = RequestMethod.GET)
	public @ResponseBody culqiBE ObtenerPersona(){
		culqiBE p = new culqiBE();
		p.setId("chr_live_NDZFIsEZbSgpeaLq");
		//Timestamp timestamp = new Timestamp(System.currentTimeMillis());		
		return p;
	}
	
	@RequestMapping(value="/persona2", method = RequestMethod.GET)
	public ResponseEntity<culqiBE> ObtenerPersona2(){
		culqiBE p = new culqiBE();
		p.setId("chr_live_NDZFIsEZbSgpeaLq");
		//Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return new ResponseEntity<culqiBE>(p,HttpStatus.OK);
	}
	
	@RequestMapping(value="/venta_app", method = RequestMethod.POST)
	public ResponseEntity<culqiBE> venta_app(@RequestParam(value="charge_amount", required=true) int charge_amount,
											 @RequestParam(value="mail", required=true) String mail,
											 @RequestParam(value="token_id", required=true) String token_id,
											 @RequestParam(value="codigo", required=true)int codigo,
											 @RequestParam(value="paci", required=true)int paci,
											 @RequestParam(value="tipproc", required=true)int tipproc,
											 @RequestParam(value="tippag", required=true)int tippag,
											 @RequestParam(value="montoproc", required=true)String montoproc,										  
											 @RequestParam(value="marcapaso", required=false)int marcapaso,
											 @RequestParam(value="distrito", required=true)String distrito,
											 @RequestParam(value="direccion", required=true)String direccion,										  
											 @RequestParam(value="latitud", required=true)String latitud,
											 @RequestParam(value="longitud", required=true)String longitud,
											 @RequestParam(value="estpago", required=false)int estpago,
											 @RequestParam(value="codpago", required=false)String codpago,
											 @RequestParam(value="fecpago", required=false)String fecpago,
											 @RequestParam(value="espta", required=false)String espta,
											 @RequestParam(value="estado_cita", required=true)int estado_cita) throws JSONException, SQLException{
		HttpStatus code = null;
		culqiBE c = new culqiBE();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet resul= null;
		Statement consult = null;
		int rs = 0;
		
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			/*Declaro los Headers*/
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-type", "application/json");
			headers.add("Authorization", "Bearer sk_live_lzZEkEZ9YutKn4m9");
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			JSONObject json = new JSONObject();
			json.put("amount", charge_amount);
			json.put("currency_code", "PEN");
			json.put("email", mail);
			json.put("source_id", token_id);
			HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);
			
			ResponseEntity<String> response = restTemplate.exchange("https://api.culqi.com/v2/charges",HttpMethod.POST, entity, String.class);
			code = response.getStatusCode();
			System.out.println("Codigo X: "+ code);
			System.out.println(response.getBody());
			JSONObject demo = new JSONObject(response.getBody());
			String id = demo.optString("id");
			c.setId(id);
			System.out.println(id);
		} catch (final HttpClientErrorException e) {
			// TODO: handle exception
			code=e.getStatusCode();
			System.out.println("CODIGO : "+e.getStatusCode());
			//e.printStackTrace();
		}
		if(code==HttpStatus.CREATED){
			try {
				Date dt = new java.util.Date();
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//String currentTime = sdf.format(dt);
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				
				con = MySQLConexion.getConexion();
				//String sql = "INSERT INTO AP_CITAS (COD_USR,PACI_ID,TIPO_PROC,TIPO_PAGO,MONTO_PROC,MARCA_PASO,CITA_DISTRITO,CITA_DIRECCION,CITA_CORD_LATITUD,CITA_CORD_LONGITUD,EST_PAGO,COD_PAGO,FEC_PAGO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				String sql = "INSERT INTO AP_CITAS (COD_USR,PACI_ID,TIPO_PROC,TIPO_PAGO,MONTO_PROC,MARCA_PASO,CITA_DISTRITO,CITA_DIRECCION,CITA_CORD_LATITUD,CITA_CORD_LONGITUD,EST_PAGO,COD_PAGO,FEC_PAGO,ID_ESPECIALIDAD_TA,CITA_ESTADO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				String sql2 = "SELECT C.CITA_ID, C.MONTO_PROC, UPPER(CONCAT(PA.PACI_NOM,' ',PA.PACI_APE_PAT, ' ', PA.PACI_APE_MAT)) AS PACIENTE, PROC.DES_NOMBRE, C.CITA_DISTRITO, C.CITA_DIRECCION, DATE_FORMAT(C.CITA_FECHA,'%d/%m/%Y %H:%i') AS FECHA, TPAGO.DES_NOMBRE AS TIPOPAGO, ESPAGO.DES_NOMBRE AS ESTADOPAGO FROM AP_CITAS C JOIN AP_PACIENTES PA ON PA.PACI_ID = C.PACI_ID JOIN AP_MUL_DET PROC ON PROC.COD_01 = C.TIPO_PROC AND PROC.ID_TABLA=2 JOIN AP_MUL_DET TPAGO ON TPAGO.COD_01 = C.TIPO_PAGO AND TPAGO.ID_TABLA=5 JOIN AP_MUL_DET ESPAGO ON ESPAGO.COD_01 = C.EST_PAGO AND ESPAGO.ID_TABLA=6 WHERE C.CITA_ID IN (SELECT LAST_INSERT_ID())";
				pst = con.prepareStatement(sql);
				consult = con.createStatement();
				pst.setInt(1, codigo);
				pst.setInt(2, paci);
				pst.setInt(3, tipproc);
				pst.setInt(4, tippag);
				pst.setString(5, montoproc);
				pst.setDouble(6, marcapaso);
				pst.setString(7, distrito);
				pst.setString(8, direccion);
				pst.setString(9, latitud);
				pst.setString(10, longitud);
				pst.setInt(11, estpago);
				pst.setString(12, c.getId());
				pst.setTimestamp(13, date);
				pst.setString(14, espta);
				pst.setInt(15, estado_cita);
				rs = pst.executeUpdate();
				
				resul = consult.executeQuery(sql2);
				resul.next();
				c.setCita_id(resul.getInt(1));
				c.setMonto_proc(resul.getString(2));
				c.setPaciente(resul.getString(3));
				c.setProcedimiento(resul.getString(4));
				c.setDistrito(resul.getString(5));
				c.setDireccion(resul.getString(6));
				c.setFecha(resul.getString(7));
				c.setTipopago(resul.getString(8));			
				c.setEstadopago(resul.getString(9));
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {
				if(con!=null) con.close();
				if(pst!=null) pst.close();
				if (resul!= null) resul.close();
				if(consult!=null) consult.close();
			}
		}
		
		return new ResponseEntity<culqiBE>(c,code);
	}
	
	@RequestMapping(value="/insertLogs", method = RequestMethod.POST)
	public ResponseEntity<String> insertLogITMS (@RequestParam(value="accion", required=true) String accion,
												 @RequestParam(value="cod_usr", required=true) int cod_usr,
												 @RequestParam(value="cod_pago", required=false) String cod_pago){
		int rs = 0;
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = MySQLConexion.getConexion();
			String sql = "INSERT INTO AP_LOGS VALUES (default,?,?,?,default)";
			pst = con.prepareStatement(sql);
			pst.setString(1, accion);
			pst.setInt(2, cod_usr);
			pst.setString(3, cod_pago);
			
			rs = pst.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(rs==1){
			return new ResponseEntity<String>(HttpStatus.CREATED);
		}else{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	
	/*BK PRUEBITA2*/	
	@RequestMapping(value="/pruebita2BK", method = RequestMethod.POST)
	public ResponseEntity<culqiBE> getget505BKB(@RequestParam(value="codigo", required=true)int codigo,
			  @RequestParam(value="paci", required=true)int paci,
			  @RequestParam(value="tipproc", required=true)int tipproc,
			  @RequestParam(value="tippag", required=true)int tippag,
			  @RequestParam(value="montoproc", required=true)String montoproc,										  
			  @RequestParam(value="marcapaso", required=false)int marcapaso,
			  @RequestParam(value="distrito", required=true)String distrito,
			  @RequestParam(value="direccion", required=true)String direccion,										  
			  @RequestParam(value="latitud", required=true)String latitud,
			  @RequestParam(value="longitud", required=true)String longitud,
			  @RequestParam(value="estpago", required=false)int estpago,
			  @RequestParam(value="codpago", required=false)String codpago,
			  @RequestParam(value="fecpago", required=false)String fecpago) throws JSONException{
		HttpStatus code = null;
		culqiBE c = new culqiBE();
		Connection con = null;
		PreparedStatement pst = null;
		int rs = 0;
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(
			        "http://localhost:8080/pe/persona2",
			        String.class);
			code = response.getStatusCode();
			System.out.println(response.getBody());
			JSONObject demo = new JSONObject(response.getBody());
			String id = demo.optString("id");
			c.setId(id);
			System.out.println(id);
		} catch (final HttpClientErrorException e) {
			// TODO: handle exception
			code=e.getStatusCode();
			System.out.println("CODIGO : "+e.getStatusCode());
			//e.printStackTrace();
		}
		if(code==HttpStatus.CREATED){
			try {
				Date dt = new java.util.Date();
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//String currentTime = sdf.format(dt);
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				
				con = MySQLConexion.getConexion();
				String sql = "INSERT INTO AP_CITAS (COD_USR,PACI_ID,TIPO_PROC,TIPO_PAGO,MONTO_PROC,MARCA_PASO,CITA_DISTRITO,CITA_DIRECCION,CITA_CORD_LATITUD,CITA_CORD_LONGITUD,EST_PAGO,COD_PAGO,FEC_PAGO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pst = con.prepareStatement(sql);
				pst.setInt(1, codigo);
				pst.setInt(2, paci);
				pst.setInt(3, tipproc);
				pst.setInt(4, tippag);
				pst.setString(5, montoproc);
				pst.setDouble(6, marcapaso);
				pst.setString(7, distrito);
				pst.setString(8, direccion);
				pst.setString(9, latitud);
				pst.setString(10, longitud);
				pst.setInt(11, estpago);
				pst.setString(12, c.getId());
				pst.setTimestamp(13, date);
				rs = pst.executeUpdate();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return new ResponseEntity<culqiBE>(c,code);
	}
	
	
	/*-----------TELECONSULTA----------*/
	@RequestMapping(value="/codTeleID", method = RequestMethod.POST, headers = {"Autorizacion=ITMSperu"})
	public ResponseEntity<Teleconsulta_ID> generaCodigoTeleconsulta() throws SQLException{
		
		Connection con = null;
		Teleconsulta_ID tel = new Teleconsulta_ID();
		int new_info = 0;
		
		try {
			con = MySQLConexionTeleconsulta.getConexion();
			Statement consult = con.createStatement();
			String sql = "select ifnull (max(inf_id),0)+1 as newcodigo from informe;";
			ResultSet resul = consult.executeQuery(sql);
			resul.next();
			new_info = resul.getInt(1);
			tel.setInfo_id_teleconsulta(new_info);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				if (con!= null) con.close();
			}
		
		if(new_info!=0){
			return new ResponseEntity<Teleconsulta_ID>(tel, HttpStatus.ACCEPTED);
		}else{
			return new ResponseEntity<Teleconsulta_ID>(tel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/inserTeleconsulta", method = RequestMethod.POST)
	public ResponseEntity<TeleasistenciaBE> insertTeleasistencia(@RequestParam(value="inf_tipo", required=true) int inf_tipo,
													   @RequestParam(value="inf_tipo_especialidad", required=true) int inf_tipo_especialidad,
													   @RequestParam(value="inf_motivo", required=true) int inf_motivo,
													   @RequestParam(value="inf_estado", required=true) int inf_estado,
													   @RequestParam(value="inf_edad", required=true) int inf_edad,
													   @RequestParam(value="pac_id", required=true) int pac_id,
													   @RequestParam(value="inf_direccion_entrega", required=true) String inf_direccion_entrega,
													   @RequestParam(value="inf_distrito_entrega", required=true) String inf_distrito_entrega,
													   @RequestParam(value="cue_dias", required=true) int cue_dias,
													   @RequestParam(value="cue_horas", required=true) int cue_horas,
													   @RequestParam(value="p1", required=true) String p1,
													   @RequestParam(value="p2", required=true) String p2,
													   @RequestParam(value="p3", required=true) String p3,
													   @RequestParam(value="p4", required=true) String p4,
													   @RequestParam(value="p5", required=true) String p5,
													   @RequestParam(value="p6", required=true) String p6,
													   @RequestParam(value="p7", required=true) String p7,
													   @RequestParam(value="p8", required=true) String p8,
													   @RequestParam(value="cita_id_para_ta", required=true) int cita_id_para_ta) throws JSONException, SQLException{
		HttpStatus code = null;
		Connection con = null;
		Connection conApp = null;
		PreparedStatement pst = null;
		int rs = 0;
		PreparedStatement pst2 = null;
		int rs2 = 0;
		PreparedStatement pst3 = null;
		int rs3 = 0;
		int new_info_id =0;
		TeleasistenciaBE ta = new TeleasistenciaBE();
		try {
			RestTemplate restTemplate = new RestTemplate();
			/*Declaro los Headers*/
			HttpHeaders headers = new HttpHeaders();
			headers.add("Autorizacion", "ITMSperu");
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/appitms/codTeleID",HttpMethod.POST, entity, String.class);
			code = response.getStatusCode();
			System.out.println("Codigo Response: "+ code);
			JSONObject demo = new JSONObject(response.getBody());
			new_info_id = demo.optInt("info_id_teleconsulta");
			System.out.println(new_info_id);
		} catch (final HttpClientErrorException e) {
			// TODO: handle exception
			e.printStackTrace();
			code=e.getStatusCode();
			System.out.println("CODIGO : "+e.getStatusCode());
			//e.printStackTrace();
		}
		if(code==HttpStatus.ACCEPTED){
			try {
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				con 	= MySQLConexionTeleconsulta.getConexion();
				conApp	= MySQLConexion.getConexion();
				/*Ingreso la CITA*/
				//String sql = "insert into informe (inf_id,inf_tipo,inf_tipo_especialidad,inf_motivo,inf_estado,inf_fecha_recepcion,inf_edad,pac_id,inf_direccion_entrega,inf_distrito_entrega) values (?,?,?,?,?,?,?,?,?,?)";
				String sql = "insert into informe set inf_id=?,inf_tipo=?,inf_tipo_especialidad=?,inf_motivo=?,inf_estado=?,inf_fecha_recepcion=?,inf_edad=?,pac_id=(select pac_id from teleasistencia.paciente where pac_id_app =?),inf_direccion_entrega=?,inf_distrito_entrega=?";
				pst = con.prepareStatement(sql);
				pst.setInt(1, new_info_id);
				pst.setInt(2, inf_tipo);
				pst.setInt(3, inf_tipo_especialidad);
				pst.setInt(4, inf_motivo);
				pst.setInt(5, inf_estado);
				pst.setTimestamp(6, date);
				pst.setInt(7, inf_edad);
				pst.setInt(8, pac_id);
				pst.setString(9, inf_direccion_entrega);
				pst.setString(10, inf_distrito_entrega);
				
				/*Segundo Query ingreso el cuestionario*/
				String sql2 = "insert into cuestionario (inf_id,cue_dias,cue_horas,p1,p2,p3,p4,p5,p6,p7,p8,cue_estado,cue_fecha_sol_medico) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				int estadoCuestionadio = 1;
				pst2 = con.prepareStatement(sql2);
				pst2.setInt(1, new_info_id);
				pst2.setInt(2, cue_dias);
				pst2.setInt(3, cue_horas);
				pst2.setString(4, p1);
				pst2.setString(5, p2);
				pst2.setString(6, p3);
				pst2.setString(7, p4);
				pst2.setString(8, p5);
				pst2.setString(9, p6);
				pst2.setString(10, p7);
				pst2.setString(11, p8);
				pst2.setInt(12, estadoCuestionadio);
				pst2.setTimestamp(13, date);
				
				/*Tercer Query*/
				String sql3 = "UPDATE AP_CITAS SET ID_INFO_ID_TA = '"+new_info_id+"' WHERE CITA_ID=?";
				pst3 = conApp.prepareStatement(sql3);
				pst3.setInt(1, cita_id_para_ta);
				
				
				rs = pst.executeUpdate();
				rs2 = pst2.executeUpdate();
				rs3 = pst3.executeUpdate();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {
				con.close();
			}			
		}
		
		if(rs==1&&rs2==1&&rs3==1){
			ta.setTele_at_id(new_info_id);
			code=HttpStatus.CREATED;
		}else{
			code=HttpStatus.BAD_REQUEST;
		}
		//System.out.println(code);
		return new ResponseEntity<TeleasistenciaBE>(ta, code);
	}
	
	
	/*-------------------*/
	@RequestMapping(value="/atencionesPac", method = RequestMethod.POST)
	public @ResponseBody ArrayList<AtencionesBE> listadoAtenciones(@RequestParam(value="cod_usr", required=true) int cod_usr) throws SQLException{
		ArrayList<AtencionesBE> lista= new ArrayList<AtencionesBE>();
		Connection con = null;
		
		try {
			con = MySQLConexionAll.getConexion();
			Statement consult = con.createStatement();
			/*String sql = "SELECT UPPER(CONCAT(P.PACI_APE_PAT,' ',P.PACI_APE_MAT, ' ', P.PACI_NOM)) AS PACIENTE, "
					+ "DATE_FORMAT(C.CITA_FECHA,'%d/%m/%Y %H:%i') AS FECHA , MD.DES_NOMBRE,  MD2.DES_NOMBRE AS ESTADO, C.CITA_ESTADO, C.ID_ESPECIALIDAD_TA, C.PACI_ID, C.CITA_DIRECCION, C.CITA_DISTRITO, DATE_FORMAT(P.PACI_FEC_NAC, '%d/%m/%Y'), C.CITA_ID, C.ID_INFO_ID_TA FROM AP_CITAS C "
					+ "JOIN AP_PACIENTES P ON P.PACI_ID = C.PACI_ID "
					+ "JOIN AP_USUARIOS USR ON USR.COD_USR = C.COD_USR LEFT "
					+ "JOIN AP_MUL_DET MD ON MD.COD_01 = C.TIPO_PROC "
					+ "LEFT JOIN AP_MUL_DET MD2 ON MD2.COD_01 = C.CITA_ESTADO "
					+ "WHERE MD.ID_TABLA=2 "
					+ "AND MD2.ID_TABLA=4 "
					+ "AND USR.COD_USR="+cod_usr+" "
					+ "ORDER BY C.CITA_FECHA DESC";*/
			String sql = "SELECT UPPER(CONCAT(P.PACI_APE_PAT,' ',P.PACI_APE_MAT, ' ', P.PACI_NOM)) AS PACIENTE, "
					+ "DATE_FORMAT(C.CITA_FECHA,'%d/%m/%Y %H:%i') AS FECHA , (case when isnull(TLA.txt_nombre) then MD.DES_NOMBRE else concat(MD.DES_NOMBRE, ' : ',TLA.txt_nombre) end) as Servicio, MD2.DES_NOMBRE AS ESTADO, C.CITA_ESTADO, C.ID_ESPECIALIDAD_TA, C.PACI_ID, C.CITA_DIRECCION, C.CITA_DISTRITO, DATE_FORMAT(P.PACI_FEC_NAC, '%d/%m/%Y'), C.CITA_ID, C.ID_INFO_ID_TA, C.TIPO_PROC, C.EXA_ID, C.INFO_ID FROM desarrollo.AP_CITAS C "
					+ "JOIN desarrollo.AP_PACIENTES P ON P.PACI_ID = C.PACI_ID "
					+ "JOIN desarrollo.AP_USUARIOS USR ON USR.COD_USR = C.COD_USR "
					+ "LEFT JOIN desarrollo.AP_MUL_DET MD ON MD.COD_01 = C.TIPO_PROC AND MD.ID_TABLA=2 "
					+ "LEFT JOIN desarrollo.AP_MUL_DET MD2 ON MD2.COD_01 = C.CITA_ESTADO AND MD2.ID_TABLA=4 "
					+ "LEFT JOIN teleasistencia.tabladet TLA ON TLA.id_registro = C.ID_ESPECIALIDAD_TA AND TLA.id_tabla=3 "
					+ "WHERE USR.COD_USR="+cod_usr+" "
					+ "ORDER BY C.CITA_FECHA DESC"; 
			ResultSet resul = consult.executeQuery(sql);
			while(resul.next()){
				AtencionesBE objAten = new AtencionesBE();
				objAten.setPaciente(resul.getString(1));
				objAten.setFecha(resul.getString(2));
				objAten.setProcedimiento(resul.getString(3));
				objAten.setEstadoExamen(resul.getString(4));
				objAten.setValEstadoExamen(resul.getInt(5));
				objAten.setId_especialidad_ta(resul.getInt(6));
				objAten.setPaci_id(resul.getInt(7));
				objAten.setDireccion(resul.getString(8));
				objAten.setDistrito(resul.getString(9));
				objAten.setFecha_nacimiento(resul.getString(10));
				objAten.setPaci_edad(calcularEdad(resul.getString(10)));
				objAten.setCita_id(resul.getInt(11));
				objAten.setInfo_id_ta(resul.getInt(12));
				objAten.setProc_id(resul.getInt(13));
				objAten.setExa_id(resul.getInt(14));
				objAten.setInfo_id(resul.getInt(15));
				lista.add(objAten);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if (con!= null) con.close();
		}
		
		return lista;
	}
	
	@RequestMapping(value="/atencionesMed", method = RequestMethod.POST)
	public @ResponseBody ArrayList<PacientesMedBE> listadoAtencionesMedico(@RequestParam(value="clie_id", required=true) String clie_id) throws SQLException{
		ArrayList<PacientesMedBE> lista= new ArrayList<PacientesMedBE>();
		Connection con = null;
		
		try {
			con = OracleConexion.getConexion();
			Statement consult = con.createStatement();			
			String sql = "SELECT UPPER(CONCAT(CONCAT(PA.PACI_APELLIDO, ' '), PA.PACI_NOMBRE)) AS PACIENTE, TE.TPOE_ID, TE.TPOE_NOMBRE,I.INFO_ID,E.EXA_ID, I.CLIE_ID, TO_CHAR(I.INFO_FECHARECIBIDO, 'dd/mm/YYYY HH24:MI') AS FECHA  FROM PIT_EXA.PIT_INFORME I JOIN PIT_EXA.PIT_EXAMEN E ON E.INFO_ID = I.INFO_ID JOIN PIT_PACI.PIT_PACIENTE PA ON PA.PACI_ID = E.PACI_ID JOIN PIT_EXA.PIT_TIPOEXAMEN TE ON TE.TPOE_ID = I.TPOE_ID WHERE I.INFO_ESTADOACTUAL IN (2,3,4,5) AND I.CLIE_ID='"+clie_id+"' ORDER BY I.INFO_FECHARECIBIDO DESC"; 
			
			ResultSet resul = consult.executeQuery(sql);
			while(resul.next()){
				PacientesMedBE objPac = new PacientesMedBE();
				objPac.setPaciente(resul.getString(1));
				objPac.setProcedimiento_id(resul.getInt(2));
				objPac.setProcedimiento(resul.getString(3));
				objPac.setInfo_id(resul.getInt(4));
				objPac.setExa_id(resul.getInt(5));
				objPac.setClie_id(resul.getString(6));
				objPac.setFecha_examen(resul.getString(7));
				lista.add(objPac);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if (con!= null) con.close();
		}
		
		return lista;
	}
	
	/*---------------------------------------*/
	
	@RequestMapping(value="/especialidadTA", method = RequestMethod.POST)
	public @ResponseBody ArrayList<EspecialidadTABE> listEspecialidad() throws SQLException{
		ArrayList<EspecialidadTABE> lista = new ArrayList<EspecialidadTABE>();
		Connection con = null;
		try {
			con = MySQLConexionTeleconsulta.getConexion();
			Statement consult = con.createStatement();
			String sql = "select id_registro, txt_nombre, cod02, int01, CONCAT(txt_nombre,'  ', cod02) as dialog from tabladet where id_tabla=3 and cod01='E' and flg_estado=1;";
			ResultSet resul = consult.executeQuery(sql);
			while(resul.next()){
				EspecialidadTABE obEsp = new EspecialidadTABE();
				obEsp.setId_procedimiento(resul.getInt(1));
				obEsp.setDes_procedimiento(resul.getString(2));
				obEsp.setPrec_procedimiento(resul.getString(3));
				obEsp.setMonto_culqui(resul.getInt(4));
				obEsp.setText_dialog(resul.getString(5));
				lista.add(obEsp);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(con!=null) con.close();
		}
		
		return lista;
	}
	
	@RequestMapping(value="/updateCitaTA", method = RequestMethod.POST)
	public ResponseEntity<String> updateCitaTA(@RequestParam(value="cita_id_ta", required=true)int cita_id_ta) throws SQLException{
		Connection con = null;
		PreparedStatement pst = null;
		int rs=0;
		
		try {
			con =  MySQLConexion.getConexion();
			String sql = "UPDATE AP_CITAS SET CITA_ESTADO = 99 WHERE CITA_ID='"+cita_id_ta+"'";
			pst = con.prepareStatement(sql);
			rs = pst.executeUpdate();
			
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {
				con.close();
				pst.close();
			}
		
		if(rs==1){
			return new ResponseEntity<String>(HttpStatus.OK);
		}else{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	
	
	
	@RequestMapping(value="/conferenciaAvaya", method = RequestMethod.POST)
	public @ResponseBody TeleasistenciaVideoBE validaVideoConferencia(@RequestParam(value="inf_id", required=true)int inf_id) throws SQLException{
		Connection con = null;
		TeleasistenciaVideoBE vi = new TeleasistenciaVideoBE();
		try {
			con = MySQLConexionTeleconsulta.getConexion();
			Statement consult = con.createStatement();
			String sql = "select i.inf_id, i.inf_tipo, i.inf_tipo_especialidad, i.inf_motivo, i.inf_estado, i.inf_fecha_recepcion, cu.cue_estado, cu.cue_fecha_sol_medico, cu.cue_fecha_ini_atencion from informe i join paciente p on p.pac_id = i.pac_id join cuestionario cu on cu.inf_id = i.inf_id where i.inf_id='"+inf_id+"'";
			ResultSet resul = consult.executeQuery(sql);
			resul.next();
			vi.setInf_id(resul.getInt(1));
			vi.setInf_tipo(resul.getInt(2));
			vi.setInf_tipo_especialidad(resul.getInt(3));
			vi.setInf_motivo(resul.getInt(4));
			vi.setInf_estado(resul.getInt(5));
			vi.setInf_fecha_recepcion(resul.getString(6));
			vi.setCue_estado(resul.getInt(7));
			vi.setInf_fecha_sol_medico(resul.getString(8));
			vi.setInf_fecha_ini_atencion(resul.getString(9));
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			if(con!=null) con.close();
		}
		
		return vi;
	}
	
	@RequestMapping(value="/validaDisponibilidad", method = RequestMethod.POST)
	public ResponseEntity<String> validaDisponibilidad() throws SQLException{
		Connection con = null;
		Statement consult = null;
		ResultSet resul = null;
		
		int resultado = 0;
		
		try {
			con = MySQLConexion.getConexion();
			consult = con.createStatement();
			String sql = "select FX_VALIDA_DISPONIBILIDAD();";
			resul = consult.executeQuery(sql);
			resul.next();
			resultado = resul.getInt(1);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null) con.close();
			if(consult!=null) consult.close();
			if(resul!=null) resul.close();
		}
		if(resultado==1){
			return new ResponseEntity<String>(HttpStatus.OK);
		}else{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/*--------------------------------------------------------------------------*/
	@RequestMapping(value="/validaDisponibilidadMedico", method = RequestMethod.POST)
	public ResponseEntity<String> validaDisponibilidadMedico() throws SQLException{
		Connection con = null;
		Statement consult = null;
		ResultSet resul = null;
		
		int resultado = 0;
		
		try {
			con = MySQLConexion.getConexion();
			consult = con.createStatement();
			String sql = "SELECT COD_01 FROM AP_MUL_DET WHERE ID_REGISTRO=24;";
			resul = consult.executeQuery(sql);
			resul.next();
			resultado = resul.getInt(1);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null) con.close();
			if(consult!=null) consult.close();
			if(resul!=null) resul.close();
		}
		if(resultado==1){
			return new ResponseEntity<String>(HttpStatus.OK);
		}else{
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/*Valido Version APP*/
	@RequestMapping(value="/versionApp", method = RequestMethod.POST)
	public @ResponseBody VersionAppBE validarVersion () throws SQLException{
		VersionAppBE ver = new VersionAppBE();
		
		Connection con =null;
		Statement consult = null;
		ResultSet resul = null;
		
		try {
			con = MySQLConexion.getConexion();
			consult = con.createStatement();
			String sql = "select COD_04 from AP_MUL_DET WHERE ID_REGISTRO=26";
			resul = consult.executeQuery(sql);
			resul.next();
			ver.setVersion_actual(resul.getString(1));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null) con.close();
			if(consult!=null) consult.close();
			if(resul!=null) resul.close();
		}
		
		return ver;
	}
	
	/*Valido si hay TA*/
	/*Valido Version APP*/
	@RequestMapping(value="/validoTAenproceso", method = RequestMethod.POST)
	public @ResponseBody TeleasistenciaProcesoBE validaSihayTA (@RequestParam(value="cod_usr", required=true)int cod_usr) throws SQLException{
		TeleasistenciaProcesoBE ta = new TeleasistenciaProcesoBE();
		
		Connection con =null;
		Statement consult = null;
		ResultSet resul = null;
		
		try {
			con = MySQLConexion.getConexion();
			consult = con.createStatement();
			String sql = "SELECT TIPO_PROC, CITA_ESTADO FROM AP_CITAS WHERE CITA_ESTADO IN (1,99) AND TIPO_PROC=9 AND COD_USR = "+cod_usr+"";
			resul = consult.executeQuery(sql);
			resul.next();
			ta.setTipo_pro(resul.getInt(1));
			ta.setEstado_pro(resul.getInt(2));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null) con.close();
			if(consult!=null) consult.close();
			if(resul!=null) resul.close();
		}
		
		return ta;
	}
	
	
	
	/*OTROS MÉTODOS*/
	
	public String getMD5(String cadena) throws Exception {
		 
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(cadena.getBytes());
 
        int size = b.length;
        StringBuilder h = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
 
            int u = b[i] & 255;
 
            if (u < 16)
            {
                h.append("0").append(Integer.toHexString(u));
            }
            else
            {
                h.append(Integer.toHexString(u));
            }
        }
        return h.toString();
    }
	
	
	public Integer calcularEdad(String fecha){
	    Date fechaNac=null;
	        try {
	            /**Se puede cambiar la mascara por el formato de la fecha
	            que se quiera recibir, por ejemplo año mes día "yyyy-MM-dd"
	            en este caso es día mes año*/
	            fechaNac = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
	        } catch (Exception ex) {
	            System.out.println("Error:"+ex);
	        }
	        Calendar fechaNacimiento = Calendar.getInstance();
	        //Se crea un objeto con la fecha actual
	        Calendar fechaActual = Calendar.getInstance();
	        //Se asigna la fecha recibida a la fecha de nacimiento.
	        fechaNacimiento.setTime(fechaNac);
	        //Se restan la fecha actual y la fecha de nacimiento
	        int año = fechaActual.get(Calendar.YEAR)- fechaNacimiento.get(Calendar.YEAR);
	        int mes =fechaActual.get(Calendar.MONTH)- fechaNacimiento.get(Calendar.MONTH);
	        int dia = fechaActual.get(Calendar.DATE)- fechaNacimiento.get(Calendar.DATE);
	        //Se ajusta el año dependiendo el mes y el día
	        if(mes<0 || (mes==0 && dia<0)){
	            año--;
	        }
	        //Regresa la edad en base a la fecha de nacimiento
	        return año;
	    }
	
	/*Metodos 2018*/
	
	@RequestMapping(value="/integracion/listServiciosDomicilab", method = RequestMethod.POST)
	public @ResponseBody ArrayList<DomicilabBE> listadoServiciosDomicilab() throws SQLException{
		ArrayList<DomicilabBE> lista = new ArrayList<DomicilabBE>();
		Connection con = null;
		ResultSet resul = null;
		try {
			con = MySQLConexion.getConexion();
			Statement consult = con.createStatement();			
			String sql = "SELECT COD_07,DES_NOMBRE,COD_04,COD_05,ESTADO FROM AP_MUL_DET WHERE ID_TABLA=9 AND ESTADO=1 ORDER BY DES_NOMBRE"; 
			
			resul = consult.executeQuery(sql);
			while(resul.next()){
				DomicilabBE objDomi = new DomicilabBE();
				objDomi.setCod_proce(resul.getString(1));
				objDomi.setNom_proce(resul.getString(2));
				objDomi.setEtiqueta_proce(resul.getString(3));
				objDomi.setPrecio_proce(resul.getInt(4));
				objDomi.setEstado_proce(resul.getInt(5));
				lista.add(objDomi);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null);con.close();
			if(resul!=null);resul.close();
		}
		return lista;
	}
	
	@RequestMapping(value="/integracion/insertRQDomicilab", method = RequestMethod.POST)
	public ResponseEntity<RQdomicilabBE> insertRQdomicilab(@RequestParam(value="cita_id", required=true)int cita_id,
														   @RequestParam(value="procds", required=true)String procds,
														   @RequestParam(value="paci_id", required=true)int paci_id) throws SQLException{
		
		Connection con = null;
		PreparedStatement pst = null;
		int rs = 0;
		RQdomicilabBE ben = new RQdomicilabBE();
		ResultSet resul= null;
		Statement consult = null;
		
		HttpStatus code = null;
		PreparedStatement pst2 = null;
		
		int rs2 = 0;
		
		try {
			con = MySQLConexion.getConexion();
			String sql = "INSERT INTO AP_RQ_DOMICILAB SET COD_ORD_SER=(SELECT FX_COD_ATEN_DMLAB()),COD_CITA_ID= ?, COD_PROCEMIENTOS=?, MATRIZ_SEND=(SELECT  CONCAT('|',FX_COD_ATEN_DMLAB(),'|',P.PACI_NOM,'|',P.PACI_APE_PAT,'|',P.PACI_APE_MAT,'|0',P.PACI_TIP_DOC,'|',P.PACI_RUT,'|', DATE_FORMAT(P.PACI_FEC_NAC,'%d/%m/%Y') ,'|',P.PACI_GENERO,'|','|',P.PACI_PHONE,'|',U.USR_CORREO,'|10499|20920|',?,'|') AS QUERYX FROM AP_PACIENTES P JOIN AP_USUARIOS U ON U.COD_USR=P.COD_USR WHERE P.PACI_ID=?)";
			String sql2 = "SELECT RQ_ID, COD_ORD_SER, COD_CITA_ID, COD_PROCEMIENTOS, MATRIZ_SEND FROM AP_RQ_DOMICILAB WHERE RQ_ID IN (SELECT LAST_INSERT_ID())";
			pst = con.prepareStatement(sql);
			
			pst.setInt(1, cita_id);
			pst.setString(2, procds);
			pst.setString(3, procds);
			pst.setInt(4, paci_id);
			
			rs = pst.executeUpdate();
			
			if(rs==1){
				code = HttpStatus.CREATED;
				consult = con.createStatement();
				resul = consult.executeQuery(sql2);
				resul.next();
				ben.setRq_id(resul.getInt(1));
				ben.setCod_ord_ser(resul.getString(2));
				ben.setCod_cita_id(resul.getInt(3));
				ben.setCod_procedimientos(resul.getString(4));
				ben.setMatriz_send(resul.getString(5));
				
				
				RestTemplate restTemplate = new RestTemplate();
				
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
				map.add("matriz", ben.getMatriz_send());				
				HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				
				ResponseEntity<String> response = restTemplate.postForEntity(
				        "http://192.168.3.249:8080/pe/pruebaCall",entity,
				        String.class);
				
				JSONObject demo = new JSONObject(response.getBody());
				int codresponse = demo.optInt("codigo");
				String bodyresponse = demo.optString("response");
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				
				/*System.out.println(response);
				System.out.println(codresponse);
				System.out.println(bodyresponse);
				*/				
				
				/*Realizo el UPDATE*/
				String sqlupdate = "UPDATE AP_RQ_DOMICILAB SET WS_CODE_RESPONSE=?,WS_BODY_RESPONSE=?, WS_FECHA=? WHERE RQ_ID=?";
				pst2 = con.prepareStatement(sqlupdate);
				pst2.setInt(1, codresponse);
				pst2.setString(2, bodyresponse);
				pst2.setTimestamp(3, date);
				pst2.setInt(4, ben.getRq_id());				
				rs2 = pst2.executeUpdate();
				
			}else{
				code = HttpStatus.BAD_REQUEST;
			}			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(con!=null) con.close();
			if(pst!=null) pst.close();
			if(pst2!=null) pst2.close();
			if(consult!=null) consult.close();
			if(resul!=null) resul.close();
		}	
		
		
		return new ResponseEntity<RQdomicilabBE>(ben,code);
	}
	
	
}
