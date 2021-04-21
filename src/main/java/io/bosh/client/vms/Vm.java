package io.bosh.client.vms;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * @author David Ehringer, Yannic Remmet.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Vm {

    private String id;
    private String vmCid;
    private List<String> ips = new ArrayList<String>();
    private List<String> dns = new ArrayList<String>();
    private String agentId;
    private String jobName;
    private String jobState;
    private int index;
    private String resourcePool;
    private VmVitals vitals;
    private boolean resurrectionPaused;

    public String getId() {
        return id;
    }

    public String getVmCid() {
        return vmCid;
    }

    public List<String> getIps() {
        return ips;
    }

    public List<String> getDns() {
        return dns;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobState() {
        return jobState;
    }

    public int getIndex() {
        return index;
    }

    public String getResourcePool() {
        return resourcePool;
    }

    public VmVitals getVitals() {
        return vitals;
    }
    
    public boolean isResurrectionPaused() {
        return resurrectionPaused;
    }

    @Override
    public String toString() {
        return "VmDetails [vmCid=" + vmCid + ", ips=" + ips + ", dns=" + dns + ", agentId="
                + agentId + ", jobName=" + jobName + ", jobState=" + jobState + ", index=" + index
                + ", resourcePool=" + resourcePool + ", vitals=" + vitals + "]";
    }

}
