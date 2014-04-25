package org.esupportail.transferts.web.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.web.utils.MyAuthenticator;

public class PersonnelComposanteConverter implements Converter{

	/**
	 * 
	 */
	private static final Logger logger = new LoggerImpl(PersonnelComposanteConverter.class);	
	
	public String getAsString(FacesContext context, UIComponent component, Object value) 
	{
//		return  String.valueOf(((PersonnelComposante) value).getUid())+"|"+
//				String.valueOf(((PersonnelComposante) value).getDisplayName())+"|"+
//				String.valueOf(((PersonnelComposante) value).getCodeComposante())+"|"+
//				String.valueOf(((PersonnelComposante) value).getSource()+"|"+
//				String.valueOf(((PersonnelComposante) value).getLibelleComposante()));
		return  String.valueOf(((PersonnelComposante) value).getUid())+"|"+
		String.valueOf(((PersonnelComposante) value).getDisplayName())+"|"+
		String.valueOf(((PersonnelComposante) value).getCodeComposante())+"|"+
		String.valueOf(((PersonnelComposante) value).getSource()+"|"+
		String.valueOf(((PersonnelComposante) value).getLibelleComposante())+"|"+
		String.valueOf(((PersonnelComposante) value).getTypePersonnel())+"|"+
		String.valueOf(((PersonnelComposante) value).getAnnee())+"|"+
		String.valueOf(((PersonnelComposante) value).getDroitSuppression())+"|"+
		String.valueOf(((PersonnelComposante) value).getDroitEditionPdf())+"|"+
		String.valueOf(((PersonnelComposante) value).getDroitAvis())+"|"+
		String.valueOf(((PersonnelComposante) value).getDroitDecision())+"|"+
		String.valueOf(((PersonnelComposante) value).getDroitDeverrouiller()));		
	}
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		PersonnelComposante category = new PersonnelComposante();
		//		      category.setId(Integer.parseInt(value));
		if (logger.isDebugEnabled())
			logger.debug("##################### tokens #####################--> " + value);
		String[] tokens = value.split("\\|");
		if (logger.isDebugEnabled())
		{
			logger.debug("##################### tokens[0] #####################--> " + tokens[0]);
			logger.debug("##################### tokens[1] #####################--> " + tokens[1]);
			logger.debug("##################### tokens[2] #####################--> " + tokens[2]);
			logger.debug("##################### tokens[3] #####################--> " + tokens[3]);
			logger.debug("##################### tokens[4] #####################--> " + tokens[4]);		
			logger.debug("##################### tokens[5] #####################--> " + tokens[5]);	
			logger.debug("##################### tokens[6] #####################--> " + tokens[6]);	
			logger.debug("##################### tokens[7] #####################--> " + tokens[7]);	
			logger.debug("##################### tokens[8] #####################--> " + tokens[8]);	
			logger.debug("##################### tokens[9] #####################--> " + tokens[9]);
			logger.debug("##################### tokens[10] #####################--> " + tokens[10]);	
			logger.debug("##################### tokens[10] #####################--> " + tokens[11]);	

		}
		category.setUid(tokens[0]);
		category.setDisplayName(tokens[1]);
		category.setCodeComposante(tokens[2]);
		category.setSource(tokens[3]);
		category.setLibelleComposante(tokens[4]);
		category.setTypePersonnel(Integer.parseInt(tokens[5]));
		category.setAnnee(Integer.parseInt(tokens[6]));		
		category.setDroitSuppression(tokens[7]);
		category.setDroitEditionPdf(tokens[8]);
		category.setDroitAvis(tokens[9]);
		category.setDroitDecision(tokens[10]);		
		category.setDroitDeverrouiller(tokens[11]);
//		category.setCodeComposante(value);
		return category;
	}



}
