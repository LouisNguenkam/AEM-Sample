package com.ngce.aem.sample.core.listeners;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
    service = EventListener.class,
    property = {
            
    })
public class TitlePropertyListener implements EventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(TitlePropertyListener.class);
    
    @Reference
    private SlingRepository repository;
    
    private Session session;
    private ObservationManager observationManager;
    
    protected void activate(ComponentContext context) throws Exception {
        registerPropertyListener();
        LOGGER.info("********added JCR event listener");
    }

    public void registerPropertyListener()
            throws Exception{
        session = repository.loginService(null, repository.getDefaultWorkspace());
        observationManager = session.getWorkspace().getObservationManager();
        
        observationManager.addEventListener(this, Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, "/", true, null, 
                null, true);
    }
    
    protected void deactivate(ComponentContext componentContext) {
        try {
            if (observationManager != null) {
                observationManager.removeEventListener(this);
                LOGGER.info("********removed JCR event listener");
            }
        }
        catch (RepositoryException re) {
            LOGGER.error("********error removing the JCR event listener", re);
        }
        finally {
            if (session != null) {
                session.logout();
                session = null;
            }
        }
    }
    
    public void onEvent(EventIterator it) {
        while (it.hasNext()) {
            Event event = it.nextEvent();
            try {
                LOGGER.info("********new property event: {}", event.getPath());
                Property changedProperty = session.getProperty(event.getPath());
                if (changedProperty.getName().equalsIgnoreCase("jcr:title")
                        && !changedProperty.getString().endsWith("!")) {
                    changedProperty.setValue(changedProperty.getString() + "!");
                    session.save();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }       
    }

}
