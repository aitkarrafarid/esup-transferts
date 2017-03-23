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
		    name="getDecisionsDossier",
		    query="SELECT decisionDossier FROM DecisionDossier decisionDossier"
		    ),
    @NamedQuery(
    name="getDecisionDossierByIdDecisionDossier",
    query="SELECT decisionDossier FROM DecisionDossier decisionDossier WHERE decisionDossier.idDecisionDossier = :idDecisionDossier"
    )
})
@Table(name = "DECISION_DOSSIER")
public class DecisionDossier implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427733337404494181L;

	/**
	 * Identifiant de l'état du dossier
	 */
	@Id
	@Column(name = "ID_DECISION_DOSSIER")
	private Integer idDecisionDossier;

	/**
	 * Libellé court etat du dossier
	 */
	@Column(name = "LIB_COURT")
	private String libelleCourtDecisionDossier;		
	
	/**
	 * Libellé court etat du dossier
	 */
	@Column(name = "LIB_LONG")
	private String libelleLongDecisionDossier;			

	/**
	 * Bean constructor.
	 */
	public DecisionDossier() {
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
		return "EtatDossier#" + hashCode() + "[idDecisionDossier=[" + idDecisionDossier + "], libelleCourtDecisionDossier=["
				+ libelleCourtDecisionDossier +"],  libelleLongDecisionDossier=["+ libelleLongDecisionDossier +"]";
	}

	public void setIdDecisionDossier(Integer idDecisionDossier) {
		this.idDecisionDossier = idDecisionDossier;
	}

	public Integer getIdDecisionDossier() {
		return idDecisionDossier;
	}

	public void setLibelleCourtDecisionDossier(
			String libelleCourtDecisionDossier) {
		this.libelleCourtDecisionDossier = libelleCourtDecisionDossier;
	}

	public String getLibelleCourtDecisionDossier() {
		return libelleCourtDecisionDossier;
	}

	public void setLibelleLongDecisionDossier(String libelleLongDecisionDossier) {
		this.libelleLongDecisionDossier = libelleLongDecisionDossier;
	}

	public String getLibelleLongDecisionDossier() {
		return libelleLongDecisionDossier;
	}

}