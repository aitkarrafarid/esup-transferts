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
		    name="getLocalisationDossier",
		    query="SELECT localisationDossier FROM LocalisationDossier localisationDossier"
		    ),
    @NamedQuery(
    name="getLocalisationDossierByIdLocalisationDossier",
    query="SELECT localisationDossier FROM LocalisationDossier localisationDossier WHERE localisationDossier.idLocalisationDossier = :idLocalisationDossier"
    )
})
@Table(name = "LOCALISATION_DOSSIER")
public class LocalisationDossier implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427710297404494181L;

	/**
	 * Identifiant de l'etat du dossier
	 */
	@Id
	@Column(name = "ID_LOCALISATION_DOSSIER")
	private Integer idLocalisationDossier;

	/**
	 * Libellé court etat du dossier
	 */
	@Column(name = "LIB_COURT")
	private String libelleCourtLocalisationDossier;		
	
	/**
	 * Libellé court etat du dossier
	 */
	@Column(name = "LIB_LONG")
	private String libelleLongLocalisationDossier;			

	/**
	 * Bean constructor.
	 */
	public LocalisationDossier() {
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

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LocalisationDossier#" + hashCode() + "[idLocalisationDossier=[" + idLocalisationDossier + "], libelleCourtLocalisationDossier=["
				+ libelleCourtLocalisationDossier +"],  libelleLongLocalisationDossier=["+ libelleLongLocalisationDossier +"]";
	}

	public void setIdLocalisationDossier(Integer idLocalisationDossier) {
		this.idLocalisationDossier = idLocalisationDossier;
	}

	public Integer getIdLocalisationDossier() {
		return idLocalisationDossier;
	}

	public void setLibelleCourtLocalisationDossier(
			String libelleCourtLocalisationDossier) {
		this.libelleCourtLocalisationDossier = libelleCourtLocalisationDossier;
	}

	public String getLibelleCourtLocalisationDossier() {
		return libelleCourtLocalisationDossier;
	}

	public void setLibelleLongLocalisationDossier(
			String libelleLongLocalisationDossier) {
		this.libelleLongLocalisationDossier = libelleLongLocalisationDossier;
	}

	public String getLibelleLongLocalisationDossier() {
		return libelleLongLocalisationDossier;
	}

}