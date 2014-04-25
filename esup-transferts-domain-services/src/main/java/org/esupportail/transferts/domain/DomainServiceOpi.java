/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.IndOpi;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.Test;

/**
 * @author Farid AIT KARRA (Universite d'Artois) - 2011
 * 
 */
@WebService
public interface DomainServiceOpi extends Serializable {
	
	public void addIndOpi(IndOpi opi);	
	
	public List<OffreDeFormationsDTO> getFormationsByRneAndAnnee(String rne, Integer annee);
	
	public List<OffreDeFormationsDTO> getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(Date maxDate, Integer annee, String rne);
	
	public Test getTest(Integer id);	
	
	public List<Test> getListeTests();
	
	public void addTest(Test test);

	public void addTransfertOpiToListeTransfertsAccueil(EtudiantRef etu);
}
