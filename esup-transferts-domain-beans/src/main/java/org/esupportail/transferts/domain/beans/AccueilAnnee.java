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
@Entity
@NamedQueries({
	@NamedQuery(
		    name="getAccueilAnnee",
		    query="SELECT accueilAnnee FROM AccueilAnnee accueilAnnee ORDER BY accueilAnnee.idAccueilAnnee DESC"
		    ),
	@NamedQuery(
		    name="getAccueilAnneeSansNull",
		    query="SELECT accueilAnnee FROM AccueilAnnee accueilAnnee WHERE accueilAnnee.idAccueilAnnee != 0 ORDER BY accueilAnnee.idAccueilAnnee DESC"
		    ),		    
    @NamedQuery(
    		name="getAccueilAnneeByIdAccueilAnnee",
    		query="SELECT accueilAnnee FROM AccueilAnnee accueilAnnee WHERE accueilAnnee.idAccueilAnnee = :idAccueilAnnee"
    ),
    @NamedQuery(
    		name="getAccueilAnneeByLibelle",
    		query="SELECT accueilAnnee FROM AccueilAnnee accueilAnnee WHERE accueilAnnee.libelle = :libelle")	
})
@Table(name = "ACCUEIL_ANNEE")
public class AccueilAnnee implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897404494181L;

	/**
	 * Identifiant de l'annee
	 */
	@Id
	@Column(name = "ID_ACCUEIL_ANNEE")
	private Integer idAccueilAnnee;

	/**
	 * Libelle
	 */
	@Column(name = "LIBELLE")
	private String libelle;			

	/**
	 * Bean constructor.
	 */
	public AccueilAnnee() {
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
		return "AccueilAnnee [idAccueilAnnee=" + idAccueilAnnee + ", libelle="
				+ libelle + "]";
	}

	public Integer getIdAccueilAnnee() {
		return idAccueilAnnee;
	}

	public void setIdAccueilAnnee(Integer idAccueilAnnee) {
		this.idAccueilAnnee = idAccueilAnnee;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

}