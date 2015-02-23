/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * The class that represent users.
 */
@Entity
@Table(name = "ACCUEIL_DECISION")
public class AccueilDecision implements Serializable {

	/**
	 * For serialize.
	 */
	@Transient
	private static final long serialVersionUID = 1234512397404494181L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DECISION_SEQ")
	@SequenceGenerator(name="DECISION_SEQ", sequenceName="DECISION_SEQ", allocationSize=1)
	private long id;		

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "numeroEtudiant", referencedColumnName = "numeroEtudiant"),
		@JoinColumn(name = "annee", referencedColumnName = "annee") })
	private EtudiantRef etudiant;		
	
//	@Column(name = "numeroEtudiant")
//	private String numeroEtudiant;	
//
//	@Column(name = "annee", length = 4)
//	private Integer annee;	

	/**
	 * Date de saisie
	 */
	@Column(name = "DATE_SAISIE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateSaisie;

	/**
	 * Auteur de la saisie
	 */
	@Column(name = "AUTEUR", nullable = false)
	private String auteur;	

	/**
	 * Voeux
	 */
	@Column(name = "AVIS", nullable = false, length = 1)
	private String avis;		
	
	/**
	 * Decision
	 */
	@Column(name = "DECISION")
	private String decision;		

	/**
	 * Bean constructor.
	 */
	public AccueilDecision() {
		super();
	}

//	@Override
//	public String toString() {
//		return "AccueilDecision [id=" + id + ", etudiant=" + etudiant
//				+ ", dateSaisie=" + dateSaisie + ", auteur=" + auteur
//				+ ", avis=" + avis + ", decision=" + decision + "]";
//	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public String getAvis() {
		return avis;
	}

	public void setAvis(String avis) {
		this.avis = avis;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public EtudiantRef getEtudiant() {
		return etudiant;
	}

	public void setEtudiant(EtudiantRef etudiant) {
		this.etudiant = etudiant;
	}
}