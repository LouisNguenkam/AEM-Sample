package com.ngce.aem.sample.core.listeners;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.jcr.resource.JcrResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@SuppressWarnings("deprecation")
@Component(service = EventHandler.class,
    property = {
            EventConstants.EVENT_TOPIC + "=" + ReplicationAction.EVENT_TOPIC
    },
    immediate = true)
public class ReplicationLogger implements JobConsumer, EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicationLogger.class);
    
    @Reference
    private JcrResourceResolverFactory resFactory;
    @Override
    public void handleEvent(Event event) {
        LOG.info("Handling Event");
        ReplicationAction action = ReplicationAction.fromEvent(event);
        ResourceResolver resourceResolver = null;
        if (action.getType().equals(ReplicationActionType.ACTIVATE)) {
            try {
                resourceResolver = resFactory.getAdministrativeResourceResolver(null);
                final PageManager pm = resourceResolver.adaptTo(PageManager.class);
                final Page page = pm.getContainingPage(action.getPath());
                if(page != null) {
                    LOG.info("********activation of page {} - {}", page.getTitle(), page.getPath());
                }
            }
            catch (LoginException e) {
               LOG.error("Error", e);
            }
            finally {
                if(resourceResolver != null && resourceResolver.isLive()) {
                    resourceResolver.close();
                }
            }
        }
        
        process (null);

    }

    @Override
    public JobResult process(Job job) {
        LOG.info("********processing job");
        return JobConsumer.JobResult.OK;
    }

}
