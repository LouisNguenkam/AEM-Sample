package com.ngce.aem.sample.core.servlets;

import java.io.IOException;

import javax.jcr.Repository;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component( service = Servlet.class,
    immediate = true,
    property = { 
        MyDefaultServlet.SLING_SERVLET_RESOURCE_TYPES + "=/apps/aemsample/components/structure/page",
        MyDefaultServlet.SLING_SERVLET_METHODS + "=GET",
        MyDefaultServlet.SLING_SERVLET_EXTENSIONS + "=json",
        MyDefaultServlet.SLING_SERVLET_EXTENSIONS + "=txt",
        MyDefaultServlet.SLING_SERVLET_EXTENSIONS + "=html",
        MyDefaultServlet.SLING_SERVLET_SELECTORS + "=jsondata",
        MyDefaultServlet.SLING_SERVLET_SELECTORS + "=testdata"
      })
public class MyDefaultServlet extends SlingSafeMethodsServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 5716761463021868918L;
    private static final Logger LOG = LoggerFactory.getLogger(MyDefaultServlet.class);
    public static final String SLING_SERVLET_RESOURCE_TYPES = "sling.servlet.resourceTypes";
    public static final String SLING_SERVLET_METHODS = "sling.servlet.methods";
    public static final String SLING_SERVLET_EXTENSIONS = "sling.servlet.extensions";
    public static final String SLING_SERVLET_SELECTORS = "sling.servlet.selectors";
    public static final String SLING_SERVLET_PATHS = "sling.servlet.paths";
    public static final String SLING_SEVERLET_PREFIX = "sling.servlet.prefix";
    
    @Reference
    private Repository repository;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Content-Type", "application/json");
        String[] keys = repository.getDescriptorKeys();
        JSONObject json = new JSONObject();
        for (String key : keys) {
            try {
                json.put(key, repository.getDescriptor(key));
            } catch (JSONException e) {
                LOG.warn("Repository Key error {}", key, e);
            }
        }
        
        response.getWriter().print(json.toString());
    }
    
    

}
