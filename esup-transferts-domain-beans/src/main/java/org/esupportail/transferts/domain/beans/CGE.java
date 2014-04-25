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

import org.esupportail.transferts.domain.beans.CGE;


/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="getListeCGEFromBddByAnneeAndSource",
			query="SELECT c FROM CGE c WHERE c.annee = :annee AND c.source = :source)"
			)//,
//	@NamedQuery(
//			name="getComposantesFromBddByAnneeAndSourceAndCodeComposante",
//			query="SELECT c FROM Composante c WHERE c.annee = :annee AND c.source = :source AND c.codeComposante = :codeComposante)"
//			)			
})
@IdClass(CGEPK.class)
@Table(name = "CGE")
public class CGE implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 1234532897404494181L;

	@Id
	private String codeCGE;		

	@Id
	private String source;		
	
	@Id
	private Integer annee;			
	
	@Column(name = "LIB_CGE", nullable=false)
	private String libelleCGE;	

	@Column(name = "valid_auto")
	private String validAuto;		
	
	/**
	 * Bean constructor.
	 */
	public CGE() {
		super();
	}

	public CGE(String codeCGE, String source, Integer annee, String libelleCGE, String validAuto) 
	{
		super();
		this.codeCGE = codeCGE;
		this.source = source;
		this.annee = annee;
		this.libelleCGE = libelleCGE;
		this.validAuto = validAuto;
	}

	@Override
	public String toString() {
		return "CGE [codeCGE=" + codeCGE + ", source="
				+ source + ", annee=" + annee + ", libelleComposante="
				+ libelleCGE + ", validAuto=" + validAuto + "]";
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public String getValidAuto() {
		return validAuto;
	}

	public void setValidAuto(String validAuto) {
		this.validAuto = validAuto;
	}

	public String getCodeCGE() {
		return codeCGE;
	}

	public void setCodeCGE(String codeCGE) {
		this.codeCGE = codeCGE;
	}

	public String getLibelleCGE() {
		return libelleCGE;
	}

	public void setLibelleCGE(String libelleCGE) {
		this.libelleCGE = libelleCGE;
	}
}