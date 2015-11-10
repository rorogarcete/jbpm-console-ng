/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.bd.backend.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.structure.backend.config.Added;
import org.guvnor.structure.backend.config.Removed;
import org.guvnor.structure.backend.deployment.DeploymentConfigChangedEvent;
import org.guvnor.structure.deployment.DeploymentConfigService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.exception.DeploymentException;
import org.jbpm.console.ng.bd.model.DeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.KModuleKey;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.bd.service.Initializable;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Undeploy;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class DeploymentManagerEntryPointImpl implements DeploymentManagerEntryPoint, Initializable<DeploymentUnit>, PostBuildHandler {

  private static final Logger logger = LoggerFactory.getLogger(DeploymentManagerEntryPointImpl.class);
  private static final MergeMode defaultMergeMode = MergeMode.valueOf(System.getProperty("org.kie.dd.mergemode", MergeMode.MERGE_COLLECTIONS.toString()));
 
  private boolean gitDeploymentsEnabled = Boolean.parseBoolean(System.getProperty("org.kie.git.deployments.enabled", "false"));
  private boolean autoDeployEnabled = Boolean.parseBoolean(System.getProperty("org.kie.auto.deploy.enabled", "true"));
  
  private boolean overrideDeploymentsEnabled = Boolean.parseBoolean(System.getProperty("org.kie.override.deploy.enabled", "false"));

  @Inject
  private DeploymentService deploymentService;

  @Inject
  @Named("DeployList")
  private Set<DeploymentUnit> deploymentUnits;

  @Inject
  private DeploymentConfigService deploymentConfigService;

  @Inject
  private ConfigurationService configurationService;

  @Inject
  private GuvnorM2Repository guvnorM2Repository;

  @Inject
  private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

  @Inject
  private Event<BuildResults> buildResultsEvent;

  @PostConstruct
  public void configure() {
      guvnorM2Repository.getRepositoryURL();
      String supportRuntimeDeployment = "true";
      List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
      for ( ConfigGroup globalConfigGroup : globalConfigGroups ) {
          if ( "settings".equals( globalConfigGroup.getName() ) ) {
              supportRuntimeDeployment = globalConfigGroup.getConfigItemValue("support.runtime.deploy");
              break;
          }
      }
      // if autodeploy enabled flag is explicitly set then skip checks of runtime supporting deployments
      if (autoDeployEnabled) {
        autoDeployEnabled = Boolean.parseBoolean(supportRuntimeDeployment);
      }
  }

  @Override
  public void initDeployments(Set<DeploymentUnit> deploymentUnits) {
    for (DeploymentUnit unit : deploymentUnits) {
      if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
        try {
          if ("true".equals(System.getProperty("org.kie.clean.onstartup"))) {
            cleanup(unit.getIdentifier());
          }
          deploymentService.deploy(unit);
        } catch (Exception e) {
          logger.warn("Error when deploying unit {} error message {}", unit, e.getMessage());
          logger.debug("Stacktrace:", e);
        }
      }
    }
  }

  @Override
  public void deploy(DeploymentUnitSummary unitSummary) {
    DeploymentUnit unit = null;
    if(!deploymentService.isDeployed(unitSummary.getId()) ||  
            (deploymentService.isDeployed(unitSummary.getId()) && overrideDeploymentsEnabled)
            || unitSummary.getId().contains("SNAPSHOT")){
        if (unitSummary.getType().equals("kjar")) {
          unit = new KModuleDeploymentUnit(((KModuleDeploymentUnitSummary) unitSummary).getGroupId(),
                  ((KModuleDeploymentUnitSummary) unitSummary).getArtifactId(),
                  ((KModuleDeploymentUnitSummary) unitSummary).getVersion(),
                  ((KModuleDeploymentUnitSummary) unitSummary).getKbaseName(),
                  ((KModuleDeploymentUnitSummary) unitSummary).getKsessionName());
            if (((KModuleDeploymentUnitSummary) unitSummary).getStrategy() != null) {
                ((KModuleDeploymentUnit)unit).setStrategy(RuntimeStrategy.valueOf(((KModuleDeploymentUnitSummary) unitSummary).getStrategy()));
            }

            if (((KModuleDeploymentUnitSummary) unitSummary).getMergeMode() != null) {
                ((KModuleDeploymentUnit)unit).setMergeMode(MergeMode.valueOf(((KModuleDeploymentUnitSummary) unitSummary).getMergeMode()));
            }
        }
        logger.info("Deploying unit "+ unitSummary.getId());
        boolean deployed = deploy(unit);
        if (!deployed) {
          BuildResults error = prepareBuildResults(unitSummary, "Deployment of unit " + unitSummary.getId()
                  + " failed due to it is already deployed!", Level.ERROR);
          buildResultsEvent.fire(error);
          throw new DeploymentException("unit already deployed!", null);
        }
    }else{

        BuildResults error = prepareBuildResults(unitSummary,
              "Deployment of unit " + unitSummary.getId() + " failed: "
               + "unit already deployed! (override deployment: "+overrideDeploymentsEnabled+")", Level.ERROR);
        buildResultsEvent.fire(error);
        throw new DeploymentException("unit already deployed!", null);
    }
  }

  private BuildResults prepareBuildResults(DeploymentUnitSummary unitSummary, String messageText, Level level) {
    String[] gavElemes = unitSummary.getId().split(":");
    GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
    BuildResults buildResults = new BuildResults(gav);
    BuildMessage message = new BuildMessage();
    message.setLevel(Level.ERROR);
    message.setText(messageText);
    buildResults.addBuildMessage(message);

    return buildResults;
  }

  protected boolean deploy(DeploymentUnit unit) {
    if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
      String[] gavElemes = unit.getIdentifier().split(":");
      GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
      BuildResults buildResults = new BuildResults(gav);

      try {
        deploymentService.deploy(unit);

        return true;
      } catch (Exception e) {
        BuildMessage message = new BuildMessage();
        message.setLevel(Level.ERROR);
        message.setText("Deployment of unit " + gav + " failed: " + e.getMessage());
        logger.warn("Deployment of unit " + gav + " failed: " + e.getMessage(), e);
        buildResults.addBuildMessage(message);
        throw new DeploymentException(e.getMessage(), e);
      } finally {
        buildResultsEvent.fire(buildResults);
      }
    }

    return false;
  }

  @Override
  public void undeploy(DeploymentUnitSummary unitSummary) {
    DeploymentUnit unit = null;
    if (unitSummary.getType().equals("kjar")) {
      unit = new KModuleDeploymentUnit(((KModuleDeploymentUnitSummary) unitSummary).getGroupId(),
              ((KModuleDeploymentUnitSummary) unitSummary).getArtifactId(),
              ((KModuleDeploymentUnitSummary) unitSummary).getVersion(),
              ((KModuleDeploymentUnitSummary) unitSummary).getKbaseName(),
              ((KModuleDeploymentUnitSummary) unitSummary).getKsessionName());
    }// add for vfs
    undeploy(unit);
  }

    @Override
    public void activate(DeploymentUnitSummary unitSummary) {
        deploymentService.activate(unitSummary.getId());
    }

    @Override
    public void deactivate(DeploymentUnitSummary unitSummary) {
        deploymentService.deactivate(unitSummary.getId());
    }

    protected void undeploy(DeploymentUnit unit) {
    String[] gavElemes = unit.getIdentifier().split(":");
    GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
    BuildResults buildResults = new BuildResults(gav);
    try {
      if (deploymentService.getDeployedUnit(unit.getIdentifier()) != null) {
        deploymentService.undeploy(unit);
        cleanup(unit.getIdentifier());

      }
    } catch (Exception e) {
      BuildMessage message = new BuildMessage();
      message.setLevel(Level.ERROR);
      message.setText("Undeployment of unit " + gav + " failed: " + e.getMessage());
      logger.warn("Undeployment of unit " + gav + " failed: " + e.getMessage(), e);
      buildResults.addBuildMessage(message);
      throw new DeploymentException(e.getMessage(), e);
    } finally {

      buildResultsEvent.fire(buildResults);
    }
  }

  @Override
  public void redeploy() {
    for (DeploymentUnit unit : deploymentUnits) {
      if (deploymentService.getDeployedUnit(unit.getIdentifier()) != null) {
        deploymentService.undeploy(unit);
      }
      deploymentService.deploy(unit);
    }
  }

  protected void cleanup(final String identifier) {
    String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
    if (location == null) {
      location = System.getProperty("java.io.tmpdir");
    }
    File dataDir = new File(location);
    if (dataDir.exists()) {

      String[] jbpmSerFiles = dataDir.list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {

          return name.equals(identifier + "-jbpmSessionId.ser");
        }
      });
      for (String file : jbpmSerFiles) {
        new File(dataDir, file).delete();
      }
    }
  }

  @Override
  public PageResponse<KModuleDeploymentUnitSummary> getData(final QueryFilter filter) {
    PageResponse<KModuleDeploymentUnitSummary> response = new PageResponse<KModuleDeploymentUnitSummary>();
    List<KModuleDeploymentUnitSummary> unitsIds = getDeployedUnits(filter);
    response.setStartRowIndex(filter.getOffset());
    response.setTotalRowSize(unitsIds.size());
    response.setTotalRowSizeExact(true);
    if(!unitsIds.isEmpty() && unitsIds.size() > (filter.getCount() + filter.getOffset())){
      response.setPageRowList(new ArrayList<KModuleDeploymentUnitSummary>(unitsIds.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
      response.setLastPage(false);
      
    }else{
      response.setPageRowList(new ArrayList<KModuleDeploymentUnitSummary>(unitsIds.subList(filter.getOffset(), unitsIds.size())));
      response.setLastPage(true);
      
    }
    return response;
  }

    private List<KModuleDeploymentUnitSummary> getDeployedUnits(final QueryFilter filter) {
        Collection<DeployedUnit> deployedUnits = deploymentService.getDeployedUnits();
        List<KModuleDeploymentUnitSummary> unitsIds = new ArrayList<KModuleDeploymentUnitSummary>(deployedUnits.size());
        for (DeployedUnit du : deployedUnits) {
            KModuleDeploymentUnit kdu = (KModuleDeploymentUnit) du.getDeploymentUnit();
            KModuleDeploymentUnitSummary duSummary = new KModuleDeploymentUnitSummary(kdu.getIdentifier(), kdu.getGroupId(),
                    kdu.getArtifactId(), kdu.getVersion(), kdu.getKbaseName(), kdu.getKsessionName(), kdu.getStrategy().toString(), kdu.getMergeMode().toString());
            if(filter.getParams() == null || filter.getParams().get("textSearch") == null || ((String)filter.getParams().get("textSearch")).isEmpty()){
                unitsIds.add(duSummary);
            }else if(kdu.getIdentifier().toLowerCase().contains((String)filter.getParams().get("textSearch"))){
                unitsIds.add(duSummary);
            }
            
            duSummary.setActive(du.isActive());
        }   sort(unitsIds, filter);
        return unitsIds;
    }
  
  private void sort(List<KModuleDeploymentUnitSummary> unitsIds, final QueryFilter filter){
    if(filter.getOrderBy().equals("Deployment")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getId().compareTo(o2.getId()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    }else if(filter.getOrderBy().equals("ArtifactId")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getArtifactId().compareTo(o2.getArtifactId()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }else if(filter.getOrderBy().equals("GroupId")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getGroupId().compareTo(o2.getGroupId()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }else if(filter.getOrderBy().equals("Version")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getVersion().compareTo(o2.getVersion()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }else if(filter.getOrderBy().equals("KieBaseName")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getKbaseName().compareTo(o2.getKbaseName()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }else if(filter.getOrderBy().equals("KieSessionName")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getKsessionName().compareTo(o2.getKsessionName()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }else if(filter.getOrderBy().equals("Strategy")){
      Collections.sort(unitsIds, new Comparator<KModuleDeploymentUnitSummary>() {

        @Override
        public int compare(KModuleDeploymentUnitSummary o1, KModuleDeploymentUnitSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getStrategy().compareTo(o2.getStrategy()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    
    }
  
  }

  /**
   * Reacts on events fired by deployment service upon successful deployment to
   * runtime environment so that can be stored in system repository
   *
   * @param event deploymentEvent that holds all required information from
   * runtime point of view
   */
  public void saveDeployment(@Observes @Deploy DeploymentEvent event) {
      if (!gitDeploymentsEnabled) {
          return;
      }
      if (deploymentConfigService.getDeployment(event.getDeploymentId()) == null) {
          deploymentConfigService.addDeployment(event.getDeploymentId(), event.getDeployedUnit().getDeploymentUnit());
      }
  }

  /**
   * Reacts on events fired by deployment service upon successful undeployment
   * from runtime environment so that can be stored in system repository
   *
   * @param event deploymentEvent that holds all required information from
   * runtime point of view
   */
  public void removeDeployment(@Observes @Undeploy DeploymentEvent event) {
      if (!gitDeploymentsEnabled) {
          return;
      }
      deploymentConfigService.removeDeployment(event.getDeploymentId());
  }

  /**
   * Auto deployed is called from authoring environment after successful build
   * and deploy (to maven)
   *
   * @param buildResults Maven deploy result that holds GAV to construct
   * KModuleDeploymentUnit
   */
  @Override
  public void process(BuildResults buildResults) {

    if (!buildResults.getErrorMessages().isEmpty() || !autoDeployEnabled) {
      return;
    }
    
    try {
      String id = buildResults.getGAV().getGroupId()+":"+buildResults.getGAV().getArtifactId()+":"+buildResults.getGAV().getVersion();
      KModuleDeploymentUnitSummary unit = new KModuleDeploymentUnitSummary(id,
              buildResults.getGAV().getGroupId(),
              buildResults.getGAV().getArtifactId(),
              buildResults.getGAV().getVersion(), "", "", null, null);
      if(!deploymentService.isDeployed(id) ||  
              (deploymentService.isDeployed(id) && overrideDeploymentsEnabled) ||
              id.contains("SNAPSHOT")){
        undeploy(unit);
        deploy(unit);
      }else{
        String[] gavElemes = unit.getId().split(":");
        GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
        
        BuildMessage message = new BuildMessage();
        message.setLevel(Level.ERROR);
        message.setText("Deployment of unit " + gav + " failed: " + "unit already deployed! (override deployment: "+overrideDeploymentsEnabled+")");
        buildResults.addBuildMessage(message);
         buildResultsEvent.fire(buildResults);
        throw new DeploymentException("unit already deployed! (override deployment: "+overrideDeploymentsEnabled+")", null);
      }
    } catch (Exception e) {
      BuildMessage message = new BuildMessage();
      message.setLevel(Level.ERROR);
      message.setText("Deployment of unit " + buildResults.getGAV() + " failed: " + e.getMessage());
      buildResults.addBuildMessage(message);
      // always catch exceptions to not break originator of the event
      logger.error("Deployment of unit {} failed: {}", buildResults.getGAV(), e.getMessage(), e);
    }
  }

  /**
   * Reacts on events fired based on changes to system repository - important in
   * cluster environment where system repo will be synchronized
   *
   * @param event - event that carries the complete DeploymentUnit to be
   * undeployed
   */
  public void undeployOnEvent(@Observes @Removed DeploymentConfigChangedEvent event) {
    String deploymentId = ((DeploymentUnit) event.getDeploymentUnit()).getIdentifier();
    try {

      undeploy((DeploymentUnit) event.getDeploymentUnit());
    } catch (RuntimeException e) {
      // in case undeloy failed it might be due to active process instances and thus it shall be kept in system repo
      if (deploymentConfigService.getDeployment(deploymentId) == null) {
        deploymentConfigService.addDeployment(deploymentId, event.getDeploymentUnit());
      }
      throw e;
    }
  }

  /**
   * Reacts on events fired based on changes to system repository - important in
   * cluster environment where system repo will be synchronized
   *
   * @param event - event that carries the complete DeploymentUnit to be
   * deployed
   */
  public void deployOnEvent(@Observes @Added DeploymentConfigChangedEvent event) {
    deploy((DeploymentUnit) event.getDeploymentUnit());
  }

  @Override
  public KModuleDeploymentUnitSummary getItem(KModuleKey key) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

    @Override
    public List<KModuleDeploymentUnitSummary> getAll(QueryFilter filter) {
        return getDeployedUnits(filter);
    }


  


}
