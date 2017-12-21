package com.ngce.aem.sample.core.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables=SlingHttpServletRequest.class)
public class DateFormattingModel {

    private static final Logger LOG = LoggerFactory.getLogger(DateFormattingModel.class);
    @Inject
    private Calendar cal;
    @Inject
    private String dateFormat;
    
    public String formattedValue;
    
    @PostConstruct
    protected void init() {
        LOG.info("#### INIT CALLED #####");
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formattedValue = formatter.format(cal.getTime());
    }
    
}
