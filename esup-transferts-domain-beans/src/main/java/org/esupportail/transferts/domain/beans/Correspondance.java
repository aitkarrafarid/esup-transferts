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
import javax.xml.bind.annotation.XmlTransient;

/**
 * The class that represent users.
 */
@Entity
@Table(name = "CORRESPONDANCE")
public class Correspondance implements Serializable {

	/**
	 * For serialize.
	 */
	@Transient
	private static final long serialVersionUID = 121234597412121212L;

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CORRESPONDANCE_SEQ")
//	@SequenceGenerator(name="CORRESPONDANCE_SEQ", sequenceName="CORRESPONDANCE_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="CORRESPONDANCE_SEQ")
	@SequenceGenerator(name="CORRESPONDANCE_SEQ", sequenceName="CORRESPONDANCE_SEQ", allocationSize=1)	
	private long id;		

//	@XmlTransient
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "numeroEtudiant", referencedColumnName = "numeroEtudiant"),
		@JoinColumn(name = "annee", referencedColumnName = "annee") })
	private EtudiantRef etudiant;		
	
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
	 * titre
	 */
	@Column(name = "titre", length = 500)
	private String titre;		

	/**
	 * msg
	 */
	@Column(name = "msg", length = 2000)
	private String msg;		
	
	/**
	 * Bean constructor.
	 */
	public Correspondance() {
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

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}