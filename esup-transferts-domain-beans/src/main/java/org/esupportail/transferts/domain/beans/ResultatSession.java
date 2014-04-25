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
public class ResultatSession implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427745678404494181L;

	/**
	 * Libellé de session
	 */
	private String libSession;	
	
	/**
	 * Resultat
	 */
	private String resultat;		

	/**
	 * Mention
	 */
	private String mention;		

	/**
	 * Bean constructor.
	 */
	public ResultatSession() {
		super();
	}

	/**
	 * Bean constructor.
	 */
	public ResultatSession(String libSession, String resultat, String mention) {
		super();
		this.libSession=libSession;
		this.resultat=resultat;
		this.mention=mention;
	}	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "ResultatSession [libSession=" + libSession + ", resultat="
				+ resultat + ", mention=" + mention + "]";
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setResultat(String resultat) {
		this.resultat = resultat;
	}

	public String getResultat() {
		return resultat;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public String getMention() {
		return mention;
	}

	public void setLibSession(String libSession) {
		this.libSession = libSession;
	}

	public String getLibSession() {
		return libSession;
	}

}