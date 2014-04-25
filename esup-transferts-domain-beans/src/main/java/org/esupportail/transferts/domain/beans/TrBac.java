/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The class that represent users.
 */

public class TrBac implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 742773214251181L;

	/**
	 * Code Bac
	 */
	private String codeBac;

	/**
	 * Libellé du baccalaureat
	 */
	private String libBac;			

	/**
	 * Libellé département obtention du baccalaureat
	 */
	private String libDepBac;		
	
	/**
	 * Libellé etablissement obtention du baccalaureat
	 */
	private String libEtabBac;		
	
	/**
	 * Annee obtention du baccalaureat
	 */
	private String anneeObtentionBac;	
	
	/**
	 * Académie
	 */
	private String libelleAcademie;	
	
	/**
	 * Bean constructor.
	 */
	public TrBac() {
		super();
	}

	/**
	 * Bean constructor.
	 */
	public TrBac(String codeBac, String libBac) {
		super();
		this.codeBac=codeBac;
		this.libBac=libBac;
	}		
	
	/**
	 * Bean constructor.
	 */
	public TrBac(String codeBac, String libBac, String libDepBac, String libEtabBac, String anneeObtentionBac, String libelleAcademie) {
		super();
		this.codeBac=codeBac;
		this.libBac=libBac;
		this.libDepBac=libDepBac;
		this.libEtabBac=libEtabBac;
		this.anneeObtentionBac=anneeObtentionBac;
		this.libelleAcademie = libelleAcademie;
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

	@Override
	public String toString() {
		return "TrBac [codeBac=" + codeBac + ", libBac=" + libBac
				+ ", libDepBac=" + libDepBac + ", libEtabBac=" + libEtabBac
				+ ", anneeObtentionBac=" + anneeObtentionBac
				+ ", libelleAcademie=" + libelleAcademie + "]";
	}

	public String getCodeBac() {
		return codeBac;
	}

	public void setCodeBac(String codeBac) {
		this.codeBac = codeBac;
	}

	public String getLibBac() {
		return libBac;
	}

	public void setLibBac(String libBac) {
		this.libBac = libBac;
	}

	public String getLibDepBac() {
		return libDepBac;
	}

	public void setLibDepBac(String libDepBac) {
		this.libDepBac = libDepBac;
	}

	public String getLibEtabBac() {
		return libEtabBac;
	}

	public void setLibEtabBac(String libEtabBac) {
		this.libEtabBac = libEtabBac;
	}

	public String getAnneeObtentionBac() {
		return anneeObtentionBac;
	}

	public void setAnneeObtentionBac(String anneeObtentionBac) {
		this.anneeObtentionBac = anneeObtentionBac;
	}

	public void setLibelleAcademie(String libelleAcademie) {
		this.libelleAcademie = libelleAcademie;
	}

	public String getLibelleAcademie() {
		return libelleAcademie;
	}
}