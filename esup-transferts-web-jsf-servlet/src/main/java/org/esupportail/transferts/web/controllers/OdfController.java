package org.esupportail.transferts.web.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.ArrayUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.web.dataModel.OdfDataModel;
import org.esupportail.transferts.web.utils.FileGeneratorService;

import artois.domain.DomainService;
import artois.domain.beans.Odf;

public class OdfController extends AbstractContextAwareController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1084601237906407867L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());	
	private List<OffreDeFormationsDTO> odfs;
	private OffreDeFormationsDTO[] selectedOdfs; 
	private OdfDataModel odfDataModel;
	private DomainService domainServiceOdf;
	private List<String> listFormationsEnDoublons;
	private FileGeneratorService fileGeneratorService;
	private List<OffreDeFormationsDTO> filteredODF;  
	private Integer nbOdfs;

	public Integer getNbOdfs() {
		return nbOdfs;
	}

	public void setNbOdfs(Integer nbOdfs) {
		this.nbOdfs = nbOdfs;
	}

	@Override
	public void afterPropertiesSetInternal() {
		super.afterPropertiesSetInternal();
	}

	public  void exportXlsODF()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("exportXslODF()");
		}

		String typeExport = "xls";
		Integer anneeSuivante = getSessionController().getCurrentAnnee()+1;
		String fileName = "Offre_de_formation_" + getSessionController().getCurrentAnnee()+"-"+anneeSuivante + "." + typeExport;

		List<OffreDeFormationsDTO> list = getDomainService().getAllOffreDeFormationByAnneeAndRneAndAtifOuPas(getSessionController().getCurrentAnnee(), getSessionController().getRne());
		if(list!=null && list.size()!=0)
		{
			this.selectedOdfs = new OffreDeFormationsDTO[list.size()];
			if(logger.isDebugEnabled())
			{
				logger.debug("list --> getSessionController().getCurrentAnnee() : "+getSessionController().getCurrentAnnee());
				logger.debug("list --> list.size() : "+list.size());
			}

			for(int i=0;i<list.size();i++)
			{
				if(logger.isDebugEnabled())
					logger.debug("list --> "+list.get(i).getLibVersionEtape());					
				this.selectedOdfs[i] = list.get(i);
			}
		}

		List<String> colonnesChoisies = new ArrayList<String>();
		getFileGeneratorService().exportXlsODF(selectedOdfs, typeExport, fileName, colonnesChoisies);		
	}

	// combien de doublons dans letableau pass� en param�tre ?
	public int getNbDoublons(String[] tab)
	{
		listFormationsEnDoublons = new ArrayList<String>();
		Set<String> hs = new HashSet<String>(); 
		for (String value : tab) 
			if (!hs.add(value)) 
			{
				listFormationsEnDoublons.add(value);
				if(logger.isDebugEnabled())
					logger.debug("Found Duplicate " + value);
			}		
		return listFormationsEnDoublons.size();
	}

	public void verifier()
	{
		String[] tab = new String[selectedOdfs.length]; 
		for(int i=0;i<selectedOdfs.length;i++)
		{
			tab[i] = selectedOdfs[i].getCodeDiplome()+"|"+selectedOdfs[i].getCodeVersionDiplome()+"|"+selectedOdfs[i].getCodeEtape()+"|"+selectedOdfs[i].getCodeVersionEtape();
		}
		if(getNbDoublons(tab)==0)
		{
			String summary = "Aucune formation en doublon";
			String detail = "Aucune formation en doublon";
			Severity severity=FacesMessage.SEVERITY_INFO;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
		}
		else
		{
			String tmp="Code Diplome : ";
			for(int i=0;i<listFormationsEnDoublons.size();i++)
				tmp+=listFormationsEnDoublons.get(i).substring(0, listFormationsEnDoublons.get(i).indexOf("|"))+" - ";
			String summary = "Liste des doublons : ";
			String detail = tmp;
			if (logger.isDebugEnabled())
				logger.debug("detail des doublons -->"+detail);
			Severity severity=FacesMessage.SEVERITY_WARN;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("testDoublons");
			logger.debug("selectedOdfs.length --> "+ selectedOdfs.length);	
			logger.debug("tab.length --> "+ tab.length);			
			logger.debug("###################################### nombre de doublons dans ODF "+ getNbDoublons(tab)+" ###############################################");
		}
	}

	public void addOdfs()
	{
		List<OffreDeFormationsDTO> LOdfsLocal = getDomainService().getSelectedOdfs(getSessionController().getCurrentAnnee(),getSessionController().getRne());
		List<OffreDeFormationsDTO> listFormationsInactives = new ArrayList<OffreDeFormationsDTO>();
		List<OffreDeFormationsDTO> listFormationsIdentiques = new ArrayList<OffreDeFormationsDTO>();
		int k=0;

		if(!LOdfsLocal.isEmpty())
		{
			for(OffreDeFormationsDTO local :LOdfsLocal)
			{
				boolean test = false;
				for(OffreDeFormationsDTO odf : selectedOdfs)
				{
					if(local!=null 
							&& local.getAnnee().equals(odf.getAnnee())
							&& local.getRne().equals(odf.getRne()) 
							&& local.getCodeDiplome().equals(odf.getCodeDiplome())
							&& local.getCodeVersionDiplome().equals(odf.getCodeVersionDiplome())
							&& local.getCodeEtape().equals(odf.getCodeEtape())
							&& local.getCodeVersionEtape().equals(odf.getCodeVersionEtape())
							&& local.getCodeCentreGestion().equals(odf.getCodeCentreGestion())
							)
					{
						if (logger.isDebugEnabled())
						{
							logger.debug("1111111111");
							logger.debug("odf.getDepart()-->"+odf.getDepart());
							logger.debug("odf.getArrivee()-->"+odf.getArrivee());
						}
						test = true;
						break;
					}
				}
				if(!test)
				{
					if (logger.isDebugEnabled())
						logger.debug("2222222222");
					local.setActif(0);
					listFormationsInactives.add(local);							
				}	
				else
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("3333333333");
						logger.debug("local.getDepart()-->"+local.getDepart());
						logger.debug("local.getArrivee()-->"+local.getArrivee());
					}
					listFormationsIdentiques.add(local);
				}
			}			
		}

		OffreDeFormationsDTO[] tabFormationsInactives = new OffreDeFormationsDTO[listFormationsInactives.size()]; 
		List<OffreDeFormationsDTO> listFormationsSelectionneMoinsCellesIdentiques = new ArrayList<OffreDeFormationsDTO>();

		for(int i=0; i<listFormationsInactives.size();i++)
			tabFormationsInactives[i]=listFormationsInactives.get(i);

		for(int i=0; i<selectedOdfs.length;i++)
		{
			boolean test2=false;
			if(!listFormationsIdentiques.isEmpty())
			{
				for(OffreDeFormationsDTO o : listFormationsIdentiques)
				{
					if(o!=null 
							&& o.getAnnee().equals(selectedOdfs[i].getAnnee())
							&& o.getRne().equals(selectedOdfs[i].getRne()) 
							&& o.getCodeDiplome().equals(selectedOdfs[i].getCodeDiplome())
							&& o.getCodeVersionDiplome().equals(selectedOdfs[i].getCodeVersionDiplome())
							&& o.getCodeEtape().equals(selectedOdfs[i].getCodeEtape())
							&& o.getCodeVersionEtape().equals(selectedOdfs[i].getCodeVersionEtape())
							&& o.getCodeCentreGestion().equals(selectedOdfs[i].getCodeCentreGestion())
							)
					{
						if (logger.isDebugEnabled())
						{
							logger.debug("4444444444");
							logger.debug("o.getDepart()-->"+o.getDepart());
							logger.debug("o.getArrivee()-->"+o.getArrivee());							
							logger.debug("selectedOdfs[i].getDepart()-->"+selectedOdfs[i].getDepart());
							logger.debug("selectedOdfs[i].getArrivee()-->"+selectedOdfs[i].getArrivee());
						}
						if(o.getDepart().equals(selectedOdfs[i].getDepart())
								&& o.getArrivee().equals(selectedOdfs[i].getArrivee()))
							test2=true;
						break;
					}

				}
				if(!test2)
				{
					if (logger.isDebugEnabled())
						logger.debug("5555555555");
					selectedOdfs[i].setActif(1);
					listFormationsSelectionneMoinsCellesIdentiques.add(selectedOdfs[i]);
					k++;					
				}
			}
			else
			{
				if (logger.isDebugEnabled())
					logger.debug("6666666666");
				selectedOdfs[i].setActif(1);
				listFormationsSelectionneMoinsCellesIdentiques.add(selectedOdfs[i]);
			}
		}

		OffreDeFormationsDTO[] tabFormationsSelectionneMoinsCellesIdentiques = new OffreDeFormationsDTO[listFormationsSelectionneMoinsCellesIdentiques.size()]; 
		for(int i=0;i<listFormationsSelectionneMoinsCellesIdentiques.size();i++)
		{
			tabFormationsSelectionneMoinsCellesIdentiques[i]=listFormationsSelectionneMoinsCellesIdentiques.get(i);
		}

		OffreDeFormationsDTO[] temp3 = (OffreDeFormationsDTO[]) ArrayUtils.addAll(tabFormationsSelectionneMoinsCellesIdentiques, tabFormationsInactives);

		if(!getSessionController().isTransfertsAccueil())
		{
			if (logger.isDebugEnabled())
				logger.debug("if(!getSessionController().isTransfertsAccueil())");			
			for(int i=0; i<temp3.length;i++)
				temp3[i].setArrivee("oui");
		}	

		getDomainService().addOdfs(temp3);		

		String summary = "";
		String detail = getString("ENREGISTREMENT.ODF");
		Severity severity=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));			

		String sujet = getString("MAIL.ODF.MAJ.SUJET");
		//		String body = getString("MAIL.ODF.MAJ.BODY");
		List<WsPub> listePartenaires = getDomainService().getListeWsPub();
		String libEtab = getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getLibOffEtb();

		for(WsPub part : listePartenaires)
		{
			if(!part.getRne().equals(getSessionController().getRne()))
			{
				try {
					// smtpService.send(to, subject, htmlBody, textBody)

					String body = getString("MAIL.ODF.MAJ.BODY", libEtab);

					//				body = getString("TRANSFERT_MAIL_BODY", "aaa",
					//						"bbb") 
					//						+ getString("TRANSFERT_MAIL_BODY2")
					//						+ getString("TRANSFERT_MAIL_BODY3");

					getSmtpService().send(new InternetAddress(part.getMailCorrespondantFonctionnel()), sujet, body, body);
				} 
				catch (AddressException e) 
				{
					summary = getString("ERREUR.ENVOI_MAIL");
					detail = getString("ERREUR.ENVOI_MAIL");
					severity = FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
				}
			}
		}
	}	

	public String goToSynchroOdf() 
	{
		if (logger.isDebugEnabled())
			logger.debug("goToSynchroOdf");
		odfs = new ArrayList<OffreDeFormationsDTO>();
		this.odfDataModel = null;
		this.selectedOdfs = null;
		this.filteredODF = null;

		if(selectedOdfs==null)
		{
			List<OffreDeFormationsDTO> list = getDomainService().getSelectedOdfs(getSessionController().getCurrentAnnee(), getSessionController().getRne());
			if(list!=null && list.size()!=0)
			{
				this.selectedOdfs = new OffreDeFormationsDTO[list.size()];
				for(int i=0;i<list.size();i++)
					this.selectedOdfs[i] = list.get(i);
			}
		}					

		this.setOdfs(this.getOdfs());

		return "goToSynchroOdf";
	}	

	public Logger getLogger() {
		return logger;
	}

	public List<OffreDeFormationsDTO> getOdfs() 
	{
		//		if(getSessionController().isOdfSIScolarite())
		//		{
		if (logger.isDebugEnabled())
			logger.debug("Recuperation de l'offre de formation a partir du WebService Scolarite");
		List<OffreDeFormationsDTO> list = new ArrayList<OffreDeFormationsDTO>();
		System.out.println("################### test1");
		list = getDomainServiceScolarite().getOffreDeFormation(getSessionController().getRne(), getSessionController().getCurrentAnnee());
		nbOdfs=0;
		if(list!=null && this.selectedOdfs!=null && this.selectedOdfs.length>0)
		{
			nbOdfs=list.size();
			//				if (logger.isDebugEnabled())
			//					logger.debug("if(list!=null && this.selectedOdfs!=null && this.selectedOdfs.length>0)");
			for(int i=0;i<this.selectedOdfs.length;i++)
			{
				//					if (logger.isDebugEnabled())
				//						logger.debug("for(int i=0;i<this.selectedOdfs.length;i++)");					
				for(OffreDeFormationsDTO o : list)
				{
					//						if (logger.isDebugEnabled())
					//							logger.debug("for(OffreDeFormationsDTO o : list)");							
					if(o.getAnnee().equals(this.selectedOdfs[i].getAnnee())
							&& o.getRne().equals(this.selectedOdfs[i].getRne())
							&& o.getCodeDiplome().equals(this.selectedOdfs[i].getCodeDiplome())
							&& o.getCodeVersionDiplome().equals(this.selectedOdfs[i].getCodeVersionDiplome())
							&& o.getCodeEtape().equals(this.selectedOdfs[i].getCodeEtape())
							&& o.getCodeVersionEtape().equals(this.selectedOdfs[i].getCodeVersionEtape())
							&& o.getCodeCentreGestion().equals(this.selectedOdfs[i].getCodeCentreGestion()))
					{
						if (logger.isDebugEnabled())
						{
							logger.debug("######################################TEST EGALITE ODF###############################################");
							logger.debug("o.getAnnee()-->"+o.getAnnee());	
							logger.debug("o.getRne()-->"+o.getRne());	
							logger.debug("o.getCodeDiplome()-->"+o.getCodeDiplome());	
							logger.debug("o.getCodeVersionDiplome()-->"+o.getCodeVersionDiplome());	
							logger.debug("o.getCodeEtape()-->"+o.getCodeEtape());	
							logger.debug("o.getCodeVersionEtape()-->"+o.getCodeVersionEtape());	
							logger.debug("o.getCodeCentreGestion()-->"+o.getCodeCentreGestion());	
						}
						o.setDepart(this.selectedOdfs[i].getDepart());
						o.setArrivee(this.selectedOdfs[i].getArrivee());
					}
				}
			}
		}
		else
			if (logger.isDebugEnabled())
				logger.debug("##########################TEST NULL################################");


		System.out.println("########################## test2");
		return list;
		//		}
		//		else
		//		{
		//			if (logger.isDebugEnabled())
		//				logger.debug("Recuperation de l'offre de formation a partir du WebService WS-ODF de l'universite d'Artois");
		//			List<OffreDeFormationsDTO> list = new ArrayList<OffreDeFormationsDTO>();
		//			//			List<Odf> lOdf =  getDomainServiceOdf().getAllOdfByAnnee(getSessionController().getCurrentAnnee().toString());
		//			List<Odf> lOdf =  getSessionController().getRemoteServicesOdf().getAllOdfByAnnee(getSessionController().getCurrentAnnee().toString());
		//			nbOdfs=lOdf.size();
		//			if(!lOdf.isEmpty())
		//			{
		//				for(Odf odf : lOdf)
		//				{
		//					String niveau="";
		//					if(odf.getNiveau()==1)
		//						niveau=odf.getNiveau()+getString("INFOS.NIVEAU1");
		//					else
		//						niveau=odf.getNiveau()+getString("INFOS.NIVEAU2");
		//					OffreDeFormationsDTO o = new OffreDeFormationsDTO(odf.getRne(), 
		//							odf.getAnnee(), 
		//							odf.getCodTypDip(), 
		//							odf.getLibTypDip(), 
		//							odf.getCodeDiplome(), 
		//							odf.getCodeVersionDiplome(), 
		//							odf.getCodeEtape(), 
		//							odf.getCodeVersionEtape(), 
		//							odf.getLibDiplome(), 
		//							odf.getLibVersionEtape(), 
		//							odf.getCodeComposante(), 
		//							odf.getLibComposante(), 
		//							odf.getCodeCentreGestion(), 
		//							odf.getLibCentreGestion(),
		//							odf.getNiveau(),
		//							niveau,
		//							"oui",
		//							"oui");
		//					list.add(o);
		//				}
		//			}
		//			else
		//			{
		//				list=null;
		//			}
		//			return list;			
		//		}
	}

	public void setOdfs(List<OffreDeFormationsDTO> odfs) {
		this.odfs = odfs;
	}

	public OffreDeFormationsDTO[] getSelectedOdfs() {
		return selectedOdfs;
	}

	public void setSelectedOdfs(OffreDeFormationsDTO[] selectedOdfs) {
		this.selectedOdfs = selectedOdfs;
	}

	public OdfDataModel getOdfDataModel() {
		if(logger.isDebugEnabled() && odfs!=null)
			logger.debug("################################## odfs ######################################## --> "+odfs.size());
		if(odfs!=null)
		{
			if(logger.isDebugEnabled())
				logger.debug("################################## if(odfs!=null) ########################################");
			if(odfDataModel==null)
			{
				if(logger.isDebugEnabled())
					logger.debug("################################## if(odfDataModel==null) ########################################");
				odfDataModel = new OdfDataModel(odfs);
			}
			return odfDataModel;
		}
		else
		{
			if(logger.isDebugEnabled())
				logger.debug("################################## if(odfs==null) ########################################");
			return new OdfDataModel();
		}
	}

	public void setOdfDataModel(OdfDataModel odfDataModel) {
		this.odfDataModel = odfDataModel;
	}

	public DomainService getDomainServiceOdf() {
		return domainServiceOdf;
	}

	public void setDomainServiceOdf(DomainService domainServiceOdf) {
		this.domainServiceOdf = domainServiceOdf;
	}

	public List<String> getListFormationsEnDoublons() {
		return listFormationsEnDoublons;
	}

	public void setListFormationsEnDoublons(List<String> listFormationsEnDoublons) {
		this.listFormationsEnDoublons = listFormationsEnDoublons;
	}

	public FileGeneratorService getFileGeneratorService() {
		return fileGeneratorService;
	}

	public void setFileGeneratorService(FileGeneratorService fileGeneratorService) {
		this.fileGeneratorService = fileGeneratorService;
	}

	public List<OffreDeFormationsDTO> getFilteredODF() {
		return filteredODF;
	}

	public void setFilteredODF(List<OffreDeFormationsDTO> filteredODF) {
		this.filteredODF = filteredODF;
	}	
}