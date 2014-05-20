package org.jbpm.console.ng.gc.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WorkbenchPerspective(identifier = "Experimental Grid", isDefault = false)
public class ExperimentalGridPerspective {

	@Perspective
	public PerspectiveDefinition getPerspective() {
		final PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST);
		p.setName("Experimental Grid");
		p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Grid Experiments")));
		p.setTransient(true);
		return p;
	}
}
