package itms.com.pe.beans;

public class RQdomicilabBE {
	
	private int rq_id;
	private String cod_ord_ser;
	private int cod_cita_id;
	private String cod_procedimientos;
	private String matriz_send;
	
	public RQdomicilabBE (){}

	public int getRq_id() {
		return rq_id;
	}

	public void setRq_id(int rq_id) {
		this.rq_id = rq_id;
	}

	public String getCod_ord_ser() {
		return cod_ord_ser;
	}

	public void setCod_ord_ser(String cod_ord_ser) {
		this.cod_ord_ser = cod_ord_ser;
	}

	public int getCod_cita_id() {
		return cod_cita_id;
	}

	public void setCod_cita_id(int cod_cita_id) {
		this.cod_cita_id = cod_cita_id;
	}

	public String getCod_procedimientos() {
		return cod_procedimientos;
	}

	public void setCod_procedimientos(String cod_procedimientos) {
		this.cod_procedimientos = cod_procedimientos;
	}

	public String getMatriz_send() {
		return matriz_send;
	}

	public void setMatriz_send(String matriz_send) {
		this.matriz_send = matriz_send;
	}
	
	

}
