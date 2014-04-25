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
import javax.persistence.Transient;

import org.esupportail.transferts.domain.beans.PersonnelComposante;


/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="getListePersonnelsComposantesBySourceAndAnnee",
			query="SELECT pc FROM PersonnelComposante pc WHERE pc.source = :source AND pc.annee = :annee)"
			),	
	@NamedQuery(
			name="getListeComposantesByUidAndSourceAndAnnee",
			query="SELECT pc FROM PersonnelComposante pc WHERE pc.uid = :uid AND pc.source = :source AND pc.annee = :annee)"
			),
	@NamedQuery(
			name="getListeManagersBySourceAndAnnee",
			query="SELECT DISTINCT pc.uid, pc.displayName FROM PersonnelComposante pc WHERE pc.source = :source AND pc.annee = :annee)"
			),
	@NamedQuery(
			name="deleteListeManagersByUidAndSourceAndAnnee",
			query="delete FROM PersonnelComposante pc WHERE pc.uid = :uid AND pc.source = :source AND pc.annee = :annee"
			),
	@NamedQuery(
			name="getDroitPersonnelComposanteByUidAndSourceAndAnneeAndCodeComposante",
			query="SELECT pc FROM PersonnelComposante pc WHERE pc.uid = :uid AND pc.source = :source AND pc.annee = :annee AND pc.codeComposante = :codeComposante)"
			)	
})
@IdClass(PersonnelComposantePK.class)
@Table(name = "PERSONNEL_COMPOSANTE")
public class PersonnelComposante implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 1234532897404494181L;

	@Id
	private String uid;	

	@Id
	private String codeComposante;		

	@Id
	private String source;	
	
	@Id
	private Integer annee;		

	@Column(name = "DISPLAY_NAME", nullable=false)
	private String displayName;
	
	@Column(name = "LIB_CMP", nullable=false)
	private String libelleComposante;	
	
	/*
	 * 0 ==> Transferts départ uniquement
	 * 1 ==> Personnel de scolarite
	 * 2 ==> Autres
	 * */
	@Column(name = "TYPE_PERSONNEL", nullable=false)
	private Integer typePersonnel;	
	
	@Transient
	private String libelleTypePersonnel;
	
	@Column(name = "DROIT_SUPPRESSION", nullable=false)	
	private String droitSuppression;	

	@Column(name = "DROIT_EDITION_PDF", nullable=false)	
	private String droitEditionPdf;
	
	@Column(name = "DROIT_AVIS", nullable=false)	
	private String droitAvis;		

	@Column(name = "DROIT_DECISION", nullable=false)	
	private String droitDecision;	
	
	@Column(name = "DROIT_DEVERROUILLER", nullable=false)	
	private String droitDeverrouiller;	
	
	/**
	 * Bean constructor.
	 */
	public PersonnelComposante() {
		super();
	}

	public PersonnelComposante(String uid, String codeComposante, String source, Integer annee, String displayName, String libelleComposante) 
	{
		super();
		this.uid = uid;
		this.codeComposante = codeComposante;
		this.source = source;
		this.displayName = displayName;
		this.libelleComposante = libelleComposante;
		this.annee=annee;
	}

	public PersonnelComposante(String uid, String codeComposante, String source, Integer annee, String displayName, String libelleComposante, Integer typePersonnel) 
	{
		super();
		this.uid = uid;
		this.codeComposante = codeComposante;
		this.source = source;
		this.displayName = displayName;
		this.libelleComposante = libelleComposante;
		this.typePersonnel = typePersonnel;
		this.annee=annee;
	}

	public PersonnelComposante(String uid, 
							   String codeComposante, 
							   String source, 
							   Integer annee, 
							   String displayName, 
							   String libelleComposante, 
							   Integer typePersonnel, 
							   String droitSuppression,
							   String droitEditionPdf,
							   String droitAvis,
							   String droitDecision,
							   String droitDeverrouiller) 
	{
		super();
		this.uid = uid;
		this.codeComposante = codeComposante;
		this.source = source;
		this.displayName = displayName;
		this.libelleComposante = libelleComposante;
		this.typePersonnel = typePersonnel;
		this.annee=annee;
		this.droitSuppression=droitSuppression;
		this.droitEditionPdf=droitEditionPdf;
		this.droitAvis=droitAvis;
		this.droitDecision=droitDecision;
		this.droitDeverrouiller=droitDeverrouiller;
	}	
	
	@Override
	public String toString() {
		return "PersonnelComposante [uid=" + uid + ", codeComposante="
				+ codeComposante + ", source=" + source + ", annee=" + annee
				+ ", displayName=" + displayName + ", libelleComposante="
				+ libelleComposante + ", typePersonnel=" + typePersonnel
				+ ", libelleTypePersonnel=" + libelleTypePersonnel
				+ ", droitSuppression=" + droitSuppression
				+ ", droitEditionPdf=" + droitEditionPdf + ", droitAvis="
				+ droitAvis + ", droitDecision=" + droitDecision
				+ ", droitDeverrouiller=" + droitDeverrouiller + "]";
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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCodeComposante() {
		return codeComposante;
	}

	public void setCodeComposante(String codeComposante) {
		this.codeComposante = codeComposante;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLibelleComposante() {
		return libelleComposante;
	}

	public void setLibelleComposante(String libelleComposante) {
		this.libelleComposante = libelleComposante;
	}

	public Integer getTypePersonnel() {
		return typePersonnel;
	}

	public void setTypePersonnel(Integer typePersonnel) {
		this.typePersonnel = typePersonnel;
	}

	public String getLibelleTypePersonnel() {
		return libelleTypePersonnel;
	}

	public void setLibelleTypePersonnel(String libelleTypePersonnel) {
		this.libelleTypePersonnel = libelleTypePersonnel;
	}

	public String getDroitSuppression() {
		return droitSuppression;
	}

	public void setDroitSuppression(String droitSuppression) {
		this.droitSuppression = droitSuppression;
	}

	public String getDroitEditionPdf() {
		return droitEditionPdf;
	}

	public void setDroitEditionPdf(String droitEditionPdf) {
		this.droitEditionPdf = droitEditionPdf;
	}

	public String getDroitAvis() {
		return droitAvis;
	}

	public void setDroitAvis(String droitAvis) {
		this.droitAvis = droitAvis;
	}

	public String getDroitDecision() {
		return droitDecision;
	}

	public void setDroitDecision(String droitDecision) {
		this.droitDecision = droitDecision;
	}

	public String getDroitDeverrouiller() {
		return droitDeverrouiller;
	}

	public void setDroitDeverrouiller(String droitDeverrouiller) {
		this.droitDeverrouiller = droitDeverrouiller;
	}
}