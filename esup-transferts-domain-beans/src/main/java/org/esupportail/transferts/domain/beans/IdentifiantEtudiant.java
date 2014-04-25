/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;

import org.esupportail.transferts.domain.beans.IdentifiantEtudiant;

/**
 * The class that represent users.
 */
public class IdentifiantEtudiant implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427712345404494181L;

	private Integer codEtu;;	

	private Integer codInd;			

	private String numeroIne;

	/**
	 * Bean constructor.
	 */
	public IdentifiantEtudiant() {
		super();
	}
	
	public IdentifiantEtudiant(Integer codEtu, Integer codInd, String numeroIne) {
		super();
		this.codEtu = codEtu;
		this.codInd = codInd;
		this.numeroIne = numeroIne;
	}

	@Override
	public String toString() {
		return "IdentifiantEtudiant [codEtu=" + codEtu + ", codInd=" + codInd
				+ ", numeroIne=" + numeroIne + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getCodEtu() {
		return codEtu;
	}

	public void setCodEtu(Integer codEtu) {
		this.codEtu = codEtu;
	}

	public Integer getCodInd() {
		return codInd;
	}

	public void setCodInd(Integer codInd) {
		this.codInd = codInd;
	}

	public String getNumeroIne() {
		return numeroIne;
	}

	public void setNumeroIne(String numeroIne) {
		this.numeroIne = numeroIne;
	}


}