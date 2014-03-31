package org.jbpm.console.ng.gc.client.gridexp;

public class ProcessSummary {

    private String id;
    private String name;
    private String packageName;
    private String type;
    private String version;
    private String originalPath;
    private String deploymentId;
    private String encodedProcessSource;

    public ProcessSummary() {
    }

    public ProcessSummary(String id, String name, String deploymentId, String packageName, String type, String version,
                          String originalpath, String processSource) {
        this.id = id;
        this.name = name;
        this.deploymentId = deploymentId;
        this.packageName = packageName;
        this.type = type;
        this.version = version;
        this.originalPath = originalpath;
        this.encodedProcessSource = processSource;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId( String deploymentId ) {
        this.deploymentId = deploymentId;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

    public void setEncodedProcessSource( String encodedProcessSource ) {
        this.encodedProcessSource = encodedProcessSource;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath( String originalPath ) {
        this.originalPath = originalPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }
}
