package itms.com.pe.app;

import ir.mirrajabi.searchdialog.core.Searchable;

public class DomicilabBE implements Searchable{
	
	private String cod_proce;
	private String nom_proce;
	private String etiqueta_proce;
	private int precio_proce;
	private int estado_proce;

    public DomicilabBE(){}



	public String getCod_proce() {
		return cod_proce;
	}

	public void setCod_proce(String cod_proce) {
		this.cod_proce = cod_proce;
	}

	public String getNom_proce() {
		return nom_proce;
	}

	public void setNom_proce(String nom_proce) {
		this.nom_proce = nom_proce;
	}

	public String getEtiqueta_proce() {
		return etiqueta_proce;
	}

	public void setEtiqueta_proce(String etiqueta_proce) {
		this.etiqueta_proce = etiqueta_proce;
	}

	public int getPrecio_proce() {
		return precio_proce;
	}

	public void setPrecio_proce(int precio_proce) {
		this.precio_proce = precio_proce;
	}

	public int getEstado_proce() {
		return estado_proce;
	}

	public void setEstado_proce(int estado_proce) {
		this.estado_proce = estado_proce;
	}


	@Override
	public String getTitle() {
		return nom_proce;
	}
}
