/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="getAllCodeSize",
			query="SELECT codeSize FROM CodeSizeAnnee codeSize"
	),
	@NamedQuery(
			name="getCodeSizeByAnnee",
			query="SELECT codeSize FROM CodeSizeAnnee codeSize WHERE codeSize.annee = :annee"
	),		
	@NamedQuery(
			name="resetDefautCodeSize", 
			query="UPDATE CodeSizeAnnee codeSize SET codeSize.defaut=0"
	),
	@NamedQuery(
			name="getCodeSizeDefaut",
			query="SELECT codeSize FROM CodeSizeAnnee codeSize WHERE codeSize.defaut=1"
	),
	@NamedQuery(
			name="getAnneeCodeSize",
			query="SELECT codeSize.annee FROM CodeSizeAnnee codeSize"
	)	
})
//@Table(name = "Code_Size")
@Table(name = "CODE_SIZE")
public class CodeSizeAnnee implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897488894181L;

	/**
	 * Annee
	 */
	@Id
	@Column(name = "annee", nullable=false, length=4)
	private Integer annee;

	/**
	 * Code size (1 lettre)
	 */
	@Column(name = "code", nullable=false, length=1)
	private String code;

	/**
	 * Signature par défaut
	 */
	@Column(name = "defaut")
	private Boolean defaut;			

	/**
	 * Bean constructor.
	 */
	public CodeSizeAnnee() {
		super();
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
		return "CodeSizeAnnee [annee=" + annee + ", code=" + code + ", defaut="
		+ defaut + "]";
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setDefaut(Boolean defaut) {
		this.defaut = defaut;
	}

	public Boolean getDefaut() {
		return defaut;
	}
}