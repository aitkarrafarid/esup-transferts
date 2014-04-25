package org.esupportail.transferts.web.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.record.SSTRecord;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.TrEtablissementDTO;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;  

public class StatistiqueController extends AbstractContextAwareController implements Serializable {  

	/**
	 * 
	 */
	private static final long serialVersionUID = 711226465339167551L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());		
	private PieChartModel pieModelDepart;  
	private PieChartModel pieModelAccueil;  
	private CartesianChartModel categoryModelDetailsDepartAndArrive;  
	private CartesianChartModel categoryModelTotalDepartAndArrive;

	public StatistiqueController() {
	}  

	public final static synchronized String getInstance() {
		//une fois que l'instanciation est faite.
		return null;
	}

	public void itemSelect(ItemSelectEvent event) {  
		List<ChartSeries> allSeries = categoryModelDetailsDepartAndArrive.getSeries();
		ChartSeries selChartSeries = allSeries.get(event.getSeriesIndex());

		String rne = null;
		int j=0;
		for (Object mapKey : selChartSeries.getData().keySet()) {
			if(j==event.getItemIndex())
				rne=(String) mapKey;
			j++;
		}				    

		if (logger.isDebugEnabled()) {
			logger.debug("itemIndex -->"+event.getItemIndex());
			logger.debug("seriesIndex -->"+event.getSeriesIndex());
			logger.debug("Type de transferts -->"+selChartSeries.getLabel());
			logger.debug("Nombre total d'etablissments -->"+selChartSeries.getData().keySet().size());
			logger.debug("Rne -->"+rne);
		}
		String summary = null;
		String detail = null;

		TrEtablissementDTO etab = getDomainServiceScolarite().getEtablissementByRne(rne);

		if(etab!=null)
		{
			if (logger.isDebugEnabled())
				logger.debug("etab -->"+etab.toString());
			summary = etab.getLibEtb(); 
			detail = etab.getLibAcademie();
		}
		else
		{
			summary = "Etablissement inconnu"; 
			detail = "Etablissement inconnu";
		}
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);  
		FacesContext.getCurrentInstance().addMessage(null, msg);

		//        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",  
		//                "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex()); 
		//        FacesContext.getCurrentInstance().addMessage(null, msg);  
	}  

	private void categoryModelDetailsDepartAndArrive() {  
		categoryModelDetailsDepartAndArrive = new CartesianChartModel();  

		ChartSeries statDepart = new ChartSeries();  
		statDepart.setLabel("Depart");  
		ChartSeries statArrive = new ChartSeries();  
		statArrive.setLabel("Arrive");  

		Map<String, Long> listeStatistiquesEtabDepart = getDomainService().getStatistiquesTransfert("D", getSessionController().getCurrentAnnee());
		Map<String, Long> listeStatistiquesEtabArrive = getDomainService().getStatistiquesTransfertAccueil("A", getSessionController().getCurrentAnnee());

		if (logger.isDebugEnabled())
		{
			logger.debug("####################### size listeStatistiquesEtabDepart -->"+listeStatistiquesEtabDepart.size());
			logger.debug("####################### size listeStatistiquesEtabArrive -->"+listeStatistiquesEtabArrive.size());
		}

		Set<String> intersect = new HashSet<String>(listeStatistiquesEtabDepart.keySet());  
		intersect.retainAll(listeStatistiquesEtabArrive.keySet()); 

		Set<String> diff1 = new HashSet<String>(listeStatistiquesEtabDepart.keySet());  
		diff1.removeAll(listeStatistiquesEtabArrive.keySet()); 

		Set<String> diff2 = new HashSet<String>(listeStatistiquesEtabArrive.keySet());  
		diff2.removeAll(listeStatistiquesEtabDepart.keySet());

		Set<String> union = new HashSet<String>(diff1);  
		union.addAll(diff2); 

		for(String s : intersect)
		{
			if (logger.isDebugEnabled())
				logger.debug("########## intersect ############# -->"+s);

			for (String mapKey : listeStatistiquesEtabDepart.keySet()) 
				statDepart.set(s, listeStatistiquesEtabDepart.get(s));	

			for (String mapKey : listeStatistiquesEtabArrive.keySet()) 
				statArrive.set(s, listeStatistiquesEtabArrive.get(s));	
		}
		
		for(String s : union)
		{
			if (logger.isDebugEnabled())
				logger.debug("########## union ############# -->"+s);

			if(listeStatistiquesEtabDepart.get(s)==null)
				statDepart.set(s, 0);
			else
				statDepart.set(s, listeStatistiquesEtabDepart.get(s));	
			
			if(listeStatistiquesEtabArrive.get(s)==null)
				statArrive.set(s, 0);
			else
				statArrive.set(s, listeStatistiquesEtabArrive.get(s));	
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("####################### size depart -->"+statDepart.getData().size());
			logger.debug("####################### size arrive -->"+statArrive.getData().size());
		}
		categoryModelDetailsDepartAndArrive.addSeries(statDepart);  
		categoryModelDetailsDepartAndArrive.addSeries(statArrive);   
	} 	

	private void createCategoryModelTotalDepartAndArrive() {  
		categoryModelTotalDepartAndArrive = new CartesianChartModel();  

		ChartSeries totalDepart = new ChartSeries();
		ChartSeries totalArrive = new ChartSeries();
		totalDepart.setLabel("Depart");
		totalArrive.setLabel("Arrive");
		
		Long nbDepart = getDomainService().getStatistiquesNombreTotalTransfertDepart(getSessionController().getCurrentAnnee());
		Long nbAccueil = getDomainService().getStatistiquesNombreTotalTransfertAccueil(getSessionController().getCurrentAnnee());
		Long nbOPI = getDomainService().getStatistiquesNombreTotalTransfertOPI(getSessionController().getCurrentAnnee());
		Long AccueilPlusOPI = nbAccueil+nbOPI;
		
		totalDepart.set("", nbDepart);  
		totalArrive.set("", AccueilPlusOPI);  

		categoryModelTotalDepartAndArrive.addSeries(totalDepart);  
		categoryModelTotalDepartAndArrive.addSeries(totalArrive);  
	}  	

	public PieChartModel getPieModelDepart() {  
		createPieModelDepart();
		return pieModelDepart;  
	}  

	private void createPieModelDepart() {  
		pieModelDepart = new PieChartModel();  

		//		int demandesTransfertsByEnCoursAndAnnee = getDomainService().getDemandesTransfertsByEnCoursAndAnnee(getSessionController().getCurrentAnnee(), "D").intValue();
		//		int demandesTransfertsByAvisSaisieAndAnnee = getDomainService().getDemandesTransfertsByAvisSaisieAndAnnee(getSessionController().getCurrentAnnee(), "D").intValue();
		//		int demandesTransfertsByTerminerAndAnnee = getDomainService().getDemandesTransfertsByTerminerAndAnnee(getSessionController().getCurrentAnnee(), "D").intValue();

		int demandesTransfertsByEnCoursAndAnnee = 0;
		int demandesTransfertsByAvisSaisieAndAnnee = 1;
		int demandesTransfertsByTerminerAndAnnee = 112;		

		if(demandesTransfertsByEnCoursAndAnnee==0)
			pieModelDepart.set("Non traite", 1);  
		else
			pieModelDepart.set("Non traite", demandesTransfertsByEnCoursAndAnnee);

		if(demandesTransfertsByAvisSaisieAndAnnee==0)
			pieModelDepart.set("En cours de traitement", 1);  
		else
			pieModelDepart.set("En cours de traitement", demandesTransfertsByAvisSaisieAndAnnee);

		if(demandesTransfertsByTerminerAndAnnee==0)
			pieModelDepart.set("Traite", 1);
		else
			pieModelDepart.set("Traite", demandesTransfertsByTerminerAndAnnee);

		//				pieModel.set("Probleme de transfert OPI", getDomainService().getDemandesTransfertsByPbOpiAndAnnee(getSessionController().getCurrentAnnee()).intValue());
		//				pieModel.set("Etablissement non partenaire", getDomainService().getDemandesTransfertsByEtabNonPartenaireAndAnnee(getSessionController().getCurrentAnnee()).intValue());	
		//				pieModel.set("Infos OPI transmises", getDomainService().getDemandesTransfertsByOpiOkAndAnnee(getSessionController().getCurrentAnnee()).intValue());			

	}

	private void createPieModelAccueil() {  
		pieModelAccueil = new PieChartModel();  

		int demandesTransfertsByEnCoursAndAnnee = getDomainService().getDemandesTransfertsByEnCoursAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue();
		int demandesTransfertsByAvisSaisieAndAnnee = getDomainService().getDemandesTransfertsByAvisSaisieAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue();
		int demandesTransfertsByTerminerAndAnnee = getDomainService().getDemandesTransfertsByTerminerAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue();

		if(demandesTransfertsByEnCoursAndAnnee==0)
			pieModelAccueil.set("Non traite", 1);  
		else
			pieModelAccueil.set("Non traite", demandesTransfertsByEnCoursAndAnnee);

		if(demandesTransfertsByAvisSaisieAndAnnee==0)
			pieModelAccueil.set("En cours de traitement", 1);  
		else
			pieModelAccueil.set("En cours de traitement", demandesTransfertsByAvisSaisieAndAnnee);

		if(demandesTransfertsByTerminerAndAnnee==0)
			pieModelAccueil.set("Traite", 1);
		else
			pieModelAccueil.set("Traite", demandesTransfertsByTerminerAndAnnee);

		//		pieModelAccueil.set("Non traite", getDomainService().getDemandesTransfertsByEnCoursAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue());  
		//		pieModelAccueil.set("En cours de traitement", getDomainService().getDemandesTransfertsByAvisSaisieAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue());  
		//		pieModelAccueil.set("Traite", getDomainService().getDemandesTransfertsByTerminerAndAnnee(getSessionController().getCurrentAnnee(), "A").intValue());  
	}	

	public PieChartModel getPieModelAccueil() {
		createPieModelAccueil();
		return pieModelAccueil;
	}

	public void setPieModelAccueil(PieChartModel pieModelAccueil) {
		this.pieModelAccueil = pieModelAccueil;
	}

	public CartesianChartModel getCategoryModelTotalDepartAndArrive() {
		createCategoryModelTotalDepartAndArrive();
		return categoryModelTotalDepartAndArrive;
	}

	public void setCategoryModelTotalDepartAndArrive(CartesianChartModel categoryModelTotalDepartAndArrive) {
		this.categoryModelTotalDepartAndArrive = categoryModelTotalDepartAndArrive;
	}

	public CartesianChartModel getCategoryModelDetailsDepartAndArrive() {
		categoryModelDetailsDepartAndArrive();
		return categoryModelDetailsDepartAndArrive;
	}

	public void setCategoryModelDetailsDepartAndArrive(CartesianChartModel categoryModelDetailsDepartAndArrive) {
		this.categoryModelDetailsDepartAndArrive = categoryModelDetailsDepartAndArrive;
	}
}  