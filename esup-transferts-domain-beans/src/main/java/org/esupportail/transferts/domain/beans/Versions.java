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
		    name="getAllVersions",
		    query="SELECT versions FROM Versions versions"
		    ),
    @NamedQuery(
    name="getVersionByNumero",
    query="SELECT version FROM Versions version WHERE version.numero = :numero"
    )
})
@Table(name = "VERSIONS")
public class Versions implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897404494181L;

	/**
	 * Numéro RNE de l'établissement
	 */
	@Id
	@Column(name = "numero", nullable=false)
	private String numero;

	/**
	 * Valeur vrai ou faux pour le code paramètre
	 */
	@Column(name = "etat")
	private boolean etat;		
	
	/**
	 * Commentaire
	 */
	@Column(name = "commentaire", length = 2000)
	private String commentaire;		
	

	/**
	 * Bean constructor.
	 */
	public Versions() {
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
		return "Versions [numero=" + numero + ", etat=" + etat
				+ ", commentaire=" + commentaire + "]";
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public boolean isEtat() {
		return etat;
	}

	public void setEtat(boolean etat) {
		this.etat = etat;
	}
}