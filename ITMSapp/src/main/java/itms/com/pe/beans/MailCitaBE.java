package itms.com.pe.beans;

public class MailCitaBE {
	
	private int cita_id;
	private String monto_proc;
	private String paciente;
	private String procedimiento;
	private String distrito;
	private String direccion;
	private String fecha;
	private String tipopago;
	private String estadopago;
	private int cod_proc;
	
	public MailCitaBE(){}

	public int getCita_id() {
		return cita_id;
	}

	public void setCita_id(int cita_id) {
		this.cita_id = cita_id;
	}
	
	public String getMonto_proc() {
		return monto_proc;
	}

	public void setMonto_proc(String monto_proc) {
		this.monto_proc = monto_proc;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getProcedimiento() {
		return procedimiento;
	}

	public void setProcedimiento(String procedimiento) {
		this.procedimiento = procedimiento;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTipopago() {
		return tipopago;
	}

	public void setTipopago(String tipopago) {
		this.tipopago = tipopago;
	}

	public String getEstadopago() {
		return estadopago;
	}

	public void setEstadopago(String estadopago) {
		this.estadopago = estadopago;
	}

	public int getCod_proc() {
		return cod_proc;
	}

	public void setCod_proc(int cod_proc) {
		this.cod_proc = cod_proc;
	}
	
	

}
