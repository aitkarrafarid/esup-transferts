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

/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
		    name="allWsPub",
		    query="SELECT wsPub FROM WsPub wsPub"
		    ),
    @NamedQuery(
    name="getWsPubByRneAndAnnee",
    query="SELECT wsPub FROM WsPub wsPub WHERE wsPub.rne = :rne AND wsPub.annee=:annee"
    ),
    @NamedQuery(
    name="getWsPubByAnnee",
    query="SELECT wsPub FROM WsPub wsPub WHERE wsPub.annee=:annee"
    )    
})
@IdClass(WsPubPK.class)
//@Table(name = "WsPub")
@Table(name = "WSPUB")
public class WsPub implements Serializable, Cloneable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897404478981L;

	public Object clone()throws CloneNotSupportedException{
		return super.clone();
	}

	/**
	 * Numero RNE de l'etablissement
	 */
	@Id
	private String rne;

	/**
	 * Annee
	 */
	@Id
	private Integer annee;	
	
	/**
	 * Libelle etablissement
	 */
	@Column(name = "libEtab")
	private String libEtab;		
	
	/**
	 * Adresse du WS Transferts
	 */
	@Column(name = "url", nullable=false)
	private String url;		

	/**
	 * Identifiant du WS Transferts
	 */
	@Column(name = "identifiant", nullable=false)
	private String identifiant;		
	
	/**
	 * Mot de passe du WS Transferts
	 */
	@Column(name = "password", nullable=false)
	private String password;			

	/**
	 * Email du correspondant fonctionel à prevenir lors de la mise a jour de l'ODF
	 */
	@Column(name = "mail", nullable=false)
	private String mailCorrespondantFonctionnel;		
	
	/**
	 * Email du correspondant technique 
	 */
	@Column(name = "mail_technique", nullable=false)
	private String mailCorrespondantTechnique;


	/**
	 * Choix du voeu / compoosante ou diplome
	 */
	@Column(name = "choix_du_voeu_par_composante", nullable = false, columnDefinition = "INTEGER default 0")
	private boolean choixDuVoeuParComposante;

	@Transient
	private Integer online;
	
	@Transient
	private Integer syncOdf;	
	
	/**
	 * Bean constructor.
	 */
	public WsPub() {
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
		return "WsPub{" +
				"rne='" + rne + '\'' +
				", annee=" + annee +
				", libEtab='" + libEtab + '\'' +
				", url='" + url + '\'' +
				", identifiant='" + identifiant + '\'' +
				", password='" + password + '\'' +
				", mailCorrespondantFonctionnel='" + mailCorrespondantFonctionnel + '\'' +
				", mailCorrespondantTechnique='" + mailCorrespondantTechnique + '\'' +
				", choixDuVoeuParComposante=" + choixDuVoeuParComposante +
				", online=" + online +
				", syncOdf=" + syncOdf +
				'}';
	}

	public void setRne(String rne) {
		this.rne = rne;
	}

	public String getRne() {
		return rne;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setLibEtab(String libEtab) {
		this.libEtab = libEtab;
	}

	public String getLibEtab() {
		return libEtab;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public Integer getSyncOdf() {
		return syncOdf;
	}

	public void setSyncOdf(Integer syncOdf) {
		this.syncOdf = syncOdf;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public String getMailCorrespondantFonctionnel() {
		return mailCorrespondantFonctionnel;
	}

	public void setMailCorrespondantFonctionnel(String mailCorrespondantFonctionnel) {
		this.mailCorrespondantFonctionnel = mailCorrespondantFonctionnel;
	}

	public String getMailCorrespondantTechnique() {
		return mailCorrespondantTechnique;
	}

	public void setMailCorrespondantTechnique(String mailCorrespondantTechnique) {
		this.mailCorrespondantTechnique = mailCorrespondantTechnique;
	}

	public boolean isChoixDuVoeuParComposante() {
		return choixDuVoeuParComposante;
	}

	public void setChoixDuVoeuParComposante(boolean choixDuVoeuParComposante) {
		this.choixDuVoeuParComposante = choixDuVoeuParComposante;
	}
}