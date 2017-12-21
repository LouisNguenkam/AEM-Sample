package com.ngce.aem.sample.core.listeners;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.sling.commons.testing.jcr.RepositoryTestBase;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import com.ngce.aem.sample.core.listeners.TitlePropertyListener;

public class TitlePropertyListenerTest extends RepositoryTestBase{

    @Test
    public void testregisterPropertyListener() throws Exception{
//	    	final Repository repos = getRepository();
//        final Session session = getSession();
        TitlePropertyListener listener = new TitlePropertyListener();
        listener.registerPropertyListener();
        PowerMock.verifyAll();
    }
}
