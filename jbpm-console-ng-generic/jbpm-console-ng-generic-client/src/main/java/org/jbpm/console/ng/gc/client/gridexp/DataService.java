package org.jbpm.console.ng.gc.client.gridexp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jschatte
 * Date: 28/03/14
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public class DataService {

    List<ProcessSummary> getProcesses() {
        List<ProcessSummary> processes = new ArrayList<ProcessSummary>();
        processes.add( new ProcessSummary("p1-1", "process1", "depid1", "com.dummy", "type1", "v1", "path1", "source1"));
        processes.add( new ProcessSummary("p1-2", "process1", "depid2", "com.dummy", "type2", "v1", "path2", "source2"));
        processes.add( new ProcessSummary("p2-1", "process2", "depid3", "com.dummy", "type1", "v1", "path3", "source3"));
        processes.add( new ProcessSummary("p2-2", "process2", "depid4", "com.dummy", "type1", "v2", "path4", "source4"));
        processes.add( new ProcessSummary("p2-3", "process2", "depid5", "com.dummy", "type3", "v3", "path5", "source5"));
        return  processes;
    }

}
