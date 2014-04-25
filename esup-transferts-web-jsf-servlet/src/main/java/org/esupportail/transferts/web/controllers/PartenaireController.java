package org.esupportail.transferts.web.controllers;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.esupportail.commons.services.authentication.info.AuthInfoImpl;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.DomainServiceOpi;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.web.utils.MyAuthenticator;
import org.hibernate.mapping.Array;
import org.primefaces.event.RowEditEvent;
import org.springframework.util.Assert;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

@SuppressWarnings("restriction")
public class PartenaireController extends AbstractContextAwareController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1084601237906407867L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	private List<WsPub> partenaires;
	private List<WsPub> listePartenaires;
	private Integer timeOutConnexionWs;
	private WsPub currentPartenaire;
	private WsPub deletePartenaire;

	public PartenaireController() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void afterPropertiesSetInternal()
	{
		super.afterPropertiesSetInternal();
		Assert.notNull(timeOutConnexionWs, "property timeOutConnexionWs of class "
				+ this.getClass().getName() + " can not be null");		
	}		

	public void addPartenaire()
	{
		currentPartenaire.setAnnee(getSessionController().getCurrentAnnee());
		WsPub part = getDomainService().getWsPubByRneAndAnnee(currentPartenaire.getRne(), getSessionController().getCurrentAnnee());
		if(part!=null)
		{
			String summary = "L'etablissement partenaire existe deja pour l'annee en cours";
			String detail = "L'etablissement partenaire existe deja pour l'annee en cours";
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
		}
		else
		{
			getDomainService().addWsPub(currentPartenaire);
			listePartenaires = null;
			String summary = "Votre nouveau partenaire a bien ete bien enregistre";
			String detail = "Votre nouveau partenaire a bien ete bien enregistre";
			Severity severity=FacesMessage.SEVERITY_INFO;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
			currentPartenaire=new WsPub();
		}
	}

	public void updateOdf()
	{
		listePartenaires = null;
		this.getPartenaires();		
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mise a jour reussie", "");  
		FacesContext.getCurrentInstance().addMessage(null, msg); 		
	}

	public String goToPartenaires() 
	{
		if (logger.isDebugEnabled())
			logger.debug("goToPartenaires");
		partenaires = new ArrayList<WsPub>(); 
		currentPartenaire = new WsPub();
		listePartenaires = null;
		this.getPartenaires();
		return "goToPartenaires";
	}	

	public void onEdit(RowEditEvent event) {  
		WsPub part = getDomainService().getWsPubByRneAndAnnee(((WsPub) event.getObject()).getRne(), getSessionController().getCurrentAnnee());
		if(part!=null)
		{
			getDomainService().updateWsPub((WsPub) event.getObject());
			if (part.getUrl() != null) 
			{
				Authenticator.setDefault(new MyAuthenticator(part.getIdentifiant(), part.getPassword()));
				if (this.testUrl(part.getUrl())) 
				{
					try {
						String address = part.getUrl();
						JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
						factoryBean.setServiceClass(DomainServiceOpi.class);
						factoryBean.setAddress(address);
						DomainServiceOpi monService = (DomainServiceOpi) factoryBean.create();
						part.setOnline(1);
						if(!(part.getRne().equals(getSessionController().getRne())))
						{
							Date d = getDomainService().getDateMaxMajByRneAndAnnee(getSessionController().getCurrentAnnee(), part.getRne());
							if (logger.isDebugEnabled())
								logger.debug("######################### Date Max MAJ ################################" + d);	
							if(d!=null)
							{
								if (logger.isDebugEnabled())
									logger.debug("monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, getSessionController().getCurrentAnnee(), part.getRne());");
								List<OffreDeFormationsDTO> lOdf = monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, getSessionController().getCurrentAnnee(), part.getRne());
								if(lOdf!=null)
								{
									OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
									for(int i=0; i<lOdf.size();i++)
										tabFormationsMaj[i]=lOdf.get(i);
									getDomainService().addOdfs(tabFormationsMaj);
									part.setSyncOdf(1);
								}
								else
								{
									if (logger.isDebugEnabled())
										logger.debug("######################### Auncune Offre de formation a mettre a jour ################################");										
									part.setSyncOdf(1);
								}
							}
							else
							{
								if (logger.isDebugEnabled())
									logger.debug("monService.getFormationsByRneAndAnnee(part.getRne(), getSessionController().getCurrentAnnee());");
								List<OffreDeFormationsDTO> lOdf = monService.getFormationsByRneAndAnnee(part.getRne(), getSessionController().getCurrentAnnee());
								if(lOdf!=null)
								{
									OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
									for(int i=0; i<lOdf.size();i++)
										tabFormationsMaj[i]=lOdf.get(i);
									getDomainService().addOdfs(tabFormationsMaj);
									part.setSyncOdf(1);
								}
								else
									part.setSyncOdf(3);
							}
						}
						else
						{
							part.setSyncOdf(1);
						}
					}
					catch (Exception e) 
					{
						if (logger.isDebugEnabled()) {
							logger.debug("WebServiceException RNE : " + part.getRne());
							logger.debug("-----------------");
							logger.debug(e.getCause().getMessage());
							logger.debug("-----------------");
						}
						e.printStackTrace();
						part.setOnline(0);
						part.setSyncOdf(0);
					}					
				}
				else
				{
					part.setOnline(0);
					part.setSyncOdf(0);
				}
				AuthCacheValue.setAuthCache(new AuthCacheImpl());
				Authenticator.setDefault(null);
			}
			else
			{
				part.setOnline(0);
				part.setSyncOdf(0);
			}			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mise a jour reussie", ((WsPub) event.getObject()).getRne());  
			FacesContext.getCurrentInstance().addMessage(null, msg); 				
		}
		else
		{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de la mise a jour", ((WsPub) event.getObject()).getRne());  
			FacesContext.getCurrentInstance().addMessage(null, msg); 		
		}
		if (logger.isDebugEnabled())
			logger.debug("Rechargement du datable...");		
		partenaires = new ArrayList<WsPub>(); 
		listePartenaires = null;
		this.getPartenaires();		
	}  

	public void onCancel() {  
		if(deletePartenaire!=null)
			getDomainService().deleteWsPub(deletePartenaire);	
		else
		{
			FacesMessage msg = new FacesMessage("Veuillez contacter l'administrateur pour la suppression", "Suppression PB !!!");  
			FacesContext.getCurrentInstance().addMessage(null, msg);  
		}
		listePartenaires = null;
		this.getPartenaires();	
		deletePartenaire=null;
		FacesMessage msg = new FacesMessage("Veuillez contacter l'administrateur pour la suppression", "Suppression OK !!!");  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
	}	

	public void setOnCancel(RowEditEvent event) {  
		deletePartenaire = getDomainService().getWsPubByRneAndAnnee(((WsPub) event.getObject()).getRne(), getSessionController().getCurrentAnnee());
		FacesMessage msg = new FacesMessage("Veuillez contacter l'administrateur pour la suppression", ((WsPub) event.getObject()).getRne());  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
	}		

	public Logger getLogger() {
		return logger;
	}

	public List<WsPub> getPartenaires() 
	{
		if(listePartenaires==null)
		{
			//listePartenaires = getDomainService().getListeWsPub();
			listePartenaires = getDomainService().getWsPubByAnnee(getSessionController().getCurrentAnnee());
			for(WsPub part : listePartenaires)
			{
				if (part.getUrl() != null) 
				{
					Authenticator.setDefault(new MyAuthenticator(part.getIdentifiant(), part.getPassword()));
					if (this.testUrl(part.getUrl())) 
					{
						try {
							String address = part.getUrl();
							JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
							factoryBean.setServiceClass(DomainServiceOpi.class);
							factoryBean.setAddress(address);
							DomainServiceOpi monService = (DomainServiceOpi) factoryBean.create();
							part.setOnline(1);
							if(!(part.getRne().equals(getSessionController().getRne())))
							{
								Date d = getDomainService().getDateMaxMajByRneAndAnnee(getSessionController().getCurrentAnnee(), part.getRne());
								if (logger.isDebugEnabled())
									logger.debug("######################### Date Max MAJ ################################" + d);								
								if(d!=null)
								{
									if (logger.isDebugEnabled())
										logger.debug("monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, getSessionController().getCurrentAnnee(), part.getRne());");
									List<OffreDeFormationsDTO> lOdf = monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, getSessionController().getCurrentAnnee(), part.getRne());
									if(lOdf!=null)
									{
										OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
										for(int i=0; i<lOdf.size();i++)
											tabFormationsMaj[i]=lOdf.get(i);
										part.setSyncOdf(2);
									}
									else
									{
										part.setSyncOdf(1);
									}
								}
								else
								{
									if (logger.isDebugEnabled())
										logger.debug("monService.getFormationsByRneAndAnnee(part.getRne(), getSessionController().getCurrentAnnee());");
									List<OffreDeFormationsDTO> lOdf = monService.getFormationsByRneAndAnnee(part.getRne(), getSessionController().getCurrentAnnee());
									if(lOdf!=null)
									{
										OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
										for(int i=0; i<lOdf.size();i++)
											tabFormationsMaj[i]=lOdf.get(i);
										part.setSyncOdf(2);
									}
									else
									{
										part.setSyncOdf(3);
									}								
								}
							}
							else
							{
								part.setSyncOdf(1);
							}
						}
						catch (Exception e) 
						{
							if (logger.isDebugEnabled()) {
								logger.debug("WebServiceException RNE : " + part.getRne());
								logger.debug("-----------------");
								logger.debug(e.getCause().getMessage());
								logger.debug("-----------------");
							}
							e.printStackTrace();
							part.setOnline(0);
							part.setSyncOdf(0);
						}					
					}
					else
					{
						part.setOnline(0);
						part.setSyncOdf(0);
					}
					AuthCacheValue.setAuthCache(new AuthCacheImpl());
					Authenticator.setDefault(null);
				}
				else
				{
					part.setOnline(0);
					part.setSyncOdf(0);
				}
			}
		}
		return listePartenaires;
	}

	private boolean testUrl(String host) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
			conn.setConnectTimeout(getTimeOutConnexionWs());
			conn.connect();
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("MalformedURLException");
				logger.debug("host : " + host);
			}
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("IOException");
				logger.debug("host : " + host);
			}
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception");
				logger.debug("host : " + host);
			}
			e.printStackTrace();
			return false;				
		}
	}		

	public void setPartenaires(List<WsPub> partenaires) {
		this.partenaires = partenaires;
	}

	public Integer getTimeOutConnexionWs() {
		return timeOutConnexionWs;
	}

	public void setTimeOutConnexionWs(Integer timeOutConnexionWs) {
		this.timeOutConnexionWs = timeOutConnexionWs;
	}

	public WsPub getCurrentPartenaire() {
		return currentPartenaire;
	}

	public void setCurrentPartenaire(WsPub currentPartenaire) {
		this.currentPartenaire = currentPartenaire;
	}

	public WsPub getDeletePartenaire() {
		return deletePartenaire;
	}

	public void setDeletePartenaire(WsPub deletePartenaire) {
		this.deletePartenaire = deletePartenaire;
	}
}