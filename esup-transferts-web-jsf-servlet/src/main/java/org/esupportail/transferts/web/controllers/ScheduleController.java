package org.esupportail.transferts.web.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.Fermeture;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@ViewScoped
public class ScheduleController extends AbstractContextAwareController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1445412231455212157L;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());	

	private ScheduleModel eventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	private List<Fermeture> fermetures = null;

	private List<Fermeture> listeFermetures;
	
	private String source;

	@PostConstruct
	public void init() {
		logger.info("===>@PostConstruct---public void init() {<===");
		eventModel = new DefaultScheduleModel();
		listeFermetures = getFermetures();

		if(listeFermetures!=null)
		{
			for(Fermeture f : listeFermetures)
			{
				logger.info("fermetures---f.getIdScheduler()===>"+f.getIdScheduler()+"<===");
				ScheduleEvent e = new DefaultScheduleEvent(f.getTitre(), f.getDateDebut(), f.getDateFin());
				e.setId(f.getIdScheduler());
				//				eventModel.addEvent(e);
				eventModel.getEvents().add(e);
			}

			for(int i = 0;i<eventModel.getEventCount();i++){
				logger.info("eventModel---eventModel.getEvents().get(i).getId()===>"+eventModel.getEvents().get(i).getId()+"<===");
			}    
		}
	}

	public String goToFermetureAppliSchedulerDepart() 
	{
//		if (logger.isDebugEnabled())
			logger.info("goToFermetureAppliSchedulerDepart");
		setSource("D");
		return "goToFermetureAppli";
	}		

	public String goToFermetureAppliSchedulerAccueil() 
	{
		if (logger.isDebugEnabled())
			logger.debug("goToFermetureAppliSchedulerAccueil");
		setSource("A");
		return "goToFermetureAppli";
	}	
	
	public void addPeriodeFermeture(){
		logger.info("===> public void addPeriodeFermeture(){<===");
		List<Fermeture> l1 = new ArrayList<Fermeture>();

		logger.info("eventModel---getEventCount===>"+eventModel.getEventCount()+"<===");
		for(int i = 0;i<eventModel.getEventCount();i++){
			logger.info("eventModel---eventModel.getEvents().get(i).getId()===>"+eventModel.getEvents().get(i).getId()+"<===");
			//			logger.info("eventModel---eventModel.getEvents().get(i).getStartDate()===>"+eventModel.getEvents().get(i).getStartDate()+"<===");

			Fermeture tmp = new Fermeture();
			tmp.setIdScheduler(eventModel.getEvents().get(i).getId());
			tmp.setSource("D");
			tmp.setAnnee(2016);
			tmp.setTitre(eventModel.getEvents().get(i).getTitle());
			tmp.setDateDebut(eventModel.getEvents().get(i).getStartDate());
			tmp.setDateFin(eventModel.getEvents().get(i).getEndDate());
			tmp.setAllDay(eventModel.getEvents().get(i).isAllDay());

			logger.info("l1---eventModel.getEvents().get(i).getId()===>"+eventModel.getEvents().get(i).getId()+"<===");
			logger.info("===>#####################################################################################################################<===");
			l1.add(tmp);
		}    	
		listeFermetures = getDomainService().addPeriodeFermetures(l1);
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public void addEvent(ActionEvent actionEvent) {
		if(event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);
		this.addPeriodeFermeture();
		event = new DefaultScheduleEvent();
	}

	public void deleteEvent(ActionEvent actionEvent) {
		logger.info("===>public void deleteEvent(ActionEvent actionEvent) {<===");
		if(event.getId()!= null)
		{
			logger.info("===>event"+event.getTitle()+"<===");
			eventModel.deleteEvent(event);
		}
		getDomainService().deletePeriodeFermeture(event.getId());
		event = new DefaultScheduleEvent();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		eventModel.updateEvent(event.getScheduleEvent());
		this.addPeriodeFermeture();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}


	public List<Fermeture> getFermetures() {
		return getDomainService().getListeFermeturesBySourceAndAnnee("D", 2016);
	}


	public void setFermetures(List<Fermeture> fermetures) {
		this.fermetures = fermetures;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}