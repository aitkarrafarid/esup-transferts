/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The class that represent users.
 */
public class TrResultatVdiVetDTO implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 1234732897404494181L;


	/**
	 * Session
	 */
	private List<ResultatEtape> etapes;		
		
	/**
	 * Bean constructor.
	 */
	public TrResultatVdiVetDTO() {
		super();
	}		
	
	@Override
	public String toString() {
		return "TrResultatVdiVetDTO [etapes=" + etapes + "]";
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

	public void setEtapes(List<ResultatEtape> etapes) {
		this.etapes = etapes;
	}

	public List<ResultatEtape> getEtapes() {
		return etapes;
	}

}