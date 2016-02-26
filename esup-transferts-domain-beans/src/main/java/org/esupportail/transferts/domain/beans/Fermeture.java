/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.esupportail.transferts.domain.beans.Fermeture;


/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="getListeFermeturesBySourceAndAnnee",
			query="SELECT f FROM Fermeture f WHERE (f.annee = :annee OR f.annee = :annee - 1) AND f.source = :source)"
			)			
})
@Table(name = "FERMETURE")
public class Fermeture implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 1234532897404494181L;

	@Id
	@Column(name = "id",  nullable=false)
	private String idScheduler;		
	
	@Column(name = "source",  nullable=false)
	private String source;		
	
	@Column(name = "annee",  nullable=false)
	private Integer annee;			
	
	@Column(name = "Titre",  length = 2000)
	private String titre;	

	@Column(name = "date_debut")
	private Date dateDebut;		
	
	@Column(name = "date_fin")
	private Date dateFin;		
	
	@Column(name = "all_day")
	private boolean allDay;	
	
	/**
	 * Bean constructor.
	 */
	public Fermeture() {
		super();
	}

	public Fermeture(String idScheduler, String source, Integer annee, String titre, Date dateDebut, Date dateFin,
			boolean allDay) {
		super();
		this.idScheduler = idScheduler;
		this.source = source;
		this.annee = annee;
		this.titre = titre;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.allDay = allDay;
	}

	@Override
	public String toString() {
		return "Fermeture [idScheduler=" + idScheduler + ", source=" + source + ", annee=" + annee + ", titre=" + titre
				+ ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", allDay=" + allDay+"]";
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

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

//	public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}

	public String getIdScheduler() {
		return idScheduler;
	}

	public void setIdScheduler(String idScheduler) {
		this.idScheduler = idScheduler;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
}