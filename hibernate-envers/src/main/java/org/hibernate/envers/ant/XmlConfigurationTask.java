package org.hibernate.envers.ant;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.tool.ant.ConfigurationTask;

public class XmlConfigurationTask extends ConfigurationTask {

   //TODO: IS THIS CORRECT ??? JUST COPIED FROM JPA_CONFIGURATION TASK
   protected void doConfiguration(Configuration configuration) {
      super.doConfiguration(configuration);
      configuration.buildMappings();
      AuditConfiguration.getFor(configuration);
   }
}
