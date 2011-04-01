package org.hibernate.envers.test.integration.interfaces.hbm.propertiesAudited.subclass;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.test.integration.interfaces.hbm.propertiesAudited.AbstractPropertiesAuditedTest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Hern�n Chanfreau
 *
 */
public class SubclassPropertiesAuditedTest extends AbstractPropertiesAuditedTest {

    public void configure(Ejb3Configuration cfg) {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("mappings/interfaces/subclassPropertiesAuditedMappings.hbm.xml");
	        cfg.addFile(new File(url.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
    
}
