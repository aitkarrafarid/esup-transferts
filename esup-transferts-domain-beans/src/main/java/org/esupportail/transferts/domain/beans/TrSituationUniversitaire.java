package org.esupportail.transferts.domain.beans;


import java.io.Serializable;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Farid AIT KARRA : farid.aitkarra@univ-artois.fr
 *
 */
public class TrSituationUniversitaire implements Serializable{
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 987654321L;

	@Id
	private String id;		

	/**
	 * Annee de la situation universitaire
	 */
	private String annee;

	/**
	 * Libelle de la situation universitaire
	 */
	private String libelle;			

	/**
	 * Resultat de la situation universitaire
	 */
	private String resultat;	
	
	/**
	 * Constructeur
	 */
	public TrSituationUniversitaire(){
		super();
	}

	public TrSituationUniversitaire(String id, String annee, String libelle,
			String resultat) {
		super();
		this.id = id;
		this.annee = annee;
		this.libelle = libelle;
		this.resultat = resultat;
	}

	@Override
	public String toString() {
		return "TrSituationUniversitaire [id=" + id + ", annee=" + annee
				+ ", libelle=" + libelle + ", resultat=" + resultat + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAnnee() {
		return annee;
	}

	public void setAnnee(String annee) {
		this.annee = annee;
	}

	public String getResultat() {
		return resultat;
	}

	public void setResultat(String resultat) {
		this.resultat = resultat;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
