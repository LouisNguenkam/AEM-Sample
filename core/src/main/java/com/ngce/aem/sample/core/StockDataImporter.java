package com.ngce.aem.sample.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.polling.importer.ImportException;
import com.day.cq.polling.importer.Importer;

@Component(immediate = true,
        property = {Importer.SCHEME_PROPERTY +"=stock"},
        service = Importer.class)
public class StockDataImporter implements Importer {
    
    private static final Logger LOG = LoggerFactory.getLogger(StockDataImporter.class);
    private static final String SOURCE_URL = "http://finance.yahoo.com/d/quotes.csv?f=snd1l1yr&s=";

    @Override
    public void importData(final String scheme, final String dataSource, final Resource target) throws ImportException {
        BufferedReader in = null;
        try {
            // dataSource will be interpreted as the stock symbol
            // dataSource and target resource will be provided by the config node
            // target is the path where to write the data in the repository
            URL sourceUrl    = new URL(SOURCE_URL + dataSource);
            in = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
            String readLine = in.readLine(); // expecting only one line
            String lastTrade = Arrays.asList(Pattern.compile(",").split(readLine)).get(3);
            LOG.info("Last trade for stock symbol {} was {}", dataSource, lastTrade);
            in.close();
            
            //persist
            writeToRepository(dataSource, lastTrade, target);
        }
        catch (MalformedURLException e) {
            LOG.error("MalformedURLException", e);
        }
        catch (IOException e) {
            LOG.error("IOException", e);
        }
        catch (RepositoryException e) {
            LOG.error("RepositoryException", e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("Unable to close stream reader!", e);
                }
            }
        }

    }
    private void writeToRepository(final String stockSymbol, final String lastTrade, final Resource resource) throws RepositoryException {
        Node parent = resource.adaptTo(Node.class);
        Node stockPageNode = JcrUtil.createPath(parent.getPath() + "/" + stockSymbol, "cq:Page", 
                parent.getSession());
        Node lastTradeNode = JcrUtil.createPath(stockPageNode.getPath() + "/lastTrade", "nt:unstructured", 
                parent.getSession());
        lastTradeNode.setProperty("lastTrade", lastTrade);
        lastTradeNode.setProperty("lastUpdate", Calendar.getInstance());
        parent.getSession().save();
    }

    
    @Override
    public void importData(String scheme, String dataSource, Resource target, String login, String password)
            throws ImportException {

        importData(scheme, dataSource, target);
    }

}
