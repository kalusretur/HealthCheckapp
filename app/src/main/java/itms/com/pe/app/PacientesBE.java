package itms.com.pe.app;

public class PacientesBE {
	
	private int PACI_ID;
	private int COD_USR;
	private String PACIENTE;
	private String PACI_PHONE;
	
	
	public PacientesBE(){
		
	}


	public int getPACI_ID() {
		return PACI_ID;
	}


	public void setPACI_ID(int pACI_ID) {
		PACI_ID = pACI_ID;
	}


	public String getPACIENTE() {
		return PACIENTE;
	}


	public void setPACIENTE(String pACIENTE) {
		PACIENTE = pACIENTE;
	}

	public int getCOD_USR() {
		return COD_USR;
	}

	public void setCOD_USR(int COD_USR) {
		this.COD_USR = COD_USR;
	}

	public String getPACI_PHONE() {
		return PACI_PHONE;
	}

	public void setPACI_PHONE(String PACI_PHONE) {
		this.PACI_PHONE = PACI_PHONE;
	}
}
