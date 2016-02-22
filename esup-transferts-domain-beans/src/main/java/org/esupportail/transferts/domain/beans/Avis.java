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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.esupportail.transferts.domain.beans.Avis;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="getAvisByNumeroEtudiantAndAnnee",
			query="SELECT avis FROM Avis avis WHERE avis.numeroEtudiant = :numEtu AND avis.annee = :annee"),
	@NamedQuery(
			name="getDernierAvisFavorableByNumeroEtudiantAndAnnee",
			query="SELECT avis FROM Avis avis WHERE avis.numeroEtudiant = :numEtu AND avis.annee = :annee ORDER BY avis.dateSaisie DESC"),
	@NamedQuery(
			name="deleteAvisByNumeroEtudiantAndAnnee",
			query="delete FROM Avis avis WHERE avis.numeroEtudiant = :numEtu AND avis.annee = :annee")			
})
//@Table(name = "Avis")
@Table(name = "AVIS")
public class Avis implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897404494181L;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AVIS_SEQ")
//	@SequenceGenerator(name="AVIS_SEQ", sequenceName="AVIS_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="AVIS_SEQ")
	@SequenceGenerator(name="AVIS_SEQ", sequenceName="AVIS_SEQ", allocationSize=1)	
	private long id;	

	@Column(name = "annee", nullable=false)
	private Integer annee;			

	@Column(name="NUMERO_ETUDIANT")
	private String numeroEtudiant;

	/**
	 * date de saisie
	 */
	@Column(name = "DATE_SAISIE")
	//    @Temporal(TemporalType.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateSaisie;

	/**
	 * Identifiant de l'etat du dossier
	 */
	@Column(name = "ID_ETAT_DOSSIER")
	private Integer idEtatDossier;				

	/**
	 * Libelle de l'etat du dossier
	 */
	@Transient
	private String libEtatDossier;		

	/**
	 * Motif de l'etat du dossier
	 */
	@Column(name = "MOTIF_ETAT_DOSSIER")
	private String motifEtatDossier;		

	/**
	 * Identifiant de localisation du dossier
	 */
	@Column(name = "ID_LOCALISATION_DOSSIER")
	private Integer idLocalisationDossier;		

	/**
	 * Libelle de localisation du dossier
	 */
	@Transient
	private String libLocalisationDossier;		

	/**
	 * Motif de localisation du dossier
	 */
	@Column(name = "MOTIF_LOCALISATION_DOSSIER")
	private String motifLocalisationDossier;	

	/**
	 * Identifiant de la decision
	 */
	@Column(name = "ID_DECISION_DOSSIER")
	private Integer idDecisionDossier;		

	/**
	 * Libellé de la decision
	 */
	@Transient
	private String libDecisionDossier;		

	/**
	 * Motif de la decision
	 */
	@Column(name = "MOTIF_DECISION_DOSSIER")
	private String motifDecisionDossier;	

	/**
	 * Bean constructor.
	 */
	public Avis() {
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
		return "Avis [id=" + id + ", annee=" + annee + ", numeroEtudiant="
		+ numeroEtudiant + ", dateSaisie=" + dateSaisie
		+ ", idEtatDossier=" + idEtatDossier + ", libEtatDossier="
		+ libEtatDossier + ", motifEtatDossier=" + motifEtatDossier
		+ ", idLocalisationDossier=" + idLocalisationDossier
		+ ", libLocalisationDossier=" + libLocalisationDossier
		+ ", motifLocalisationDossier=" + motifLocalisationDossier
		+ ", idDecisionDossier=" + idDecisionDossier
		+ ", libDecisionDossier=" + libDecisionDossier
		+ ", motifDecisionDossier=" + motifDecisionDossier + "]";
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setNumeroEtudiant(String numeroEtudiant) {
		this.numeroEtudiant = numeroEtudiant;
	}

	public String getNumeroEtudiant() {
		return numeroEtudiant;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setIdEtatDossier(Integer idEtatDossier) {
		this.idEtatDossier = idEtatDossier;
	}

	public Integer getIdEtatDossier() {
		return idEtatDossier;
	}

	public void setMotifEtatDossier(String motifEtatDossier) {
		this.motifEtatDossier = motifEtatDossier;
	}

	public String getMotifEtatDossier() {
		return motifEtatDossier;
	}

	public void setIdLocalisationDossier(Integer idLocalisationDossier) {
		this.idLocalisationDossier = idLocalisationDossier;
	}

	public Integer getIdLocalisationDossier() {
		return idLocalisationDossier;
	}

	public void setMotifLocalisationDossier(String motifLocalisationDossier) {
		this.motifLocalisationDossier = motifLocalisationDossier;
	}

	public String getMotifLocalisationDossier() {
		return motifLocalisationDossier;
	}

	public void setIdDecisionDossier(Integer idDecisionDossier) {
		this.idDecisionDossier = idDecisionDossier;
	}

	public Integer getIdDecisionDossier() {
		return idDecisionDossier;
	}

	public void setMotifDecisionDossier(String motifDecisionDossier) {
		this.motifDecisionDossier = motifDecisionDossier;
	}

	public String getMotifDecisionDossier() {
		return motifDecisionDossier;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setLibEtatDossier(String libEtatDossier) {
		this.libEtatDossier = libEtatDossier;
	}

	public String getLibEtatDossier() {
		return libEtatDossier;
	}

	public void setLibLocalisationDossier(String libLocalisationDossier) {
		this.libLocalisationDossier = libLocalisationDossier;
	}

	public String getLibLocalisationDossier() {
		return libLocalisationDossier;
	}

	public void setLibDecisionDossier(String libDecisionDossier) {
		this.libDecisionDossier = libDecisionDossier;
	}

	public String getLibDecisionDossier() {
		return libDecisionDossier;
	}
}