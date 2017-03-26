package com.jvmtop.monitor;

import sun.jvmstat.monitor.*;
import sun.jvmstat.perfdata.monitor.PerfLongMonitor;
import sun.tools.jstat.Identifier;
import sun.tools.jstat.Literal;

import java.net.URISyntaxException;

/**
 * Created by liujing on 2017/2/12.
 */
public class JstatInfo {

    private String vmid_;
    private MonitoredVm vm_;

    public JstatInfo(String vmID) {
        try {
            vmid_ = vmID;
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JstatInfo(int vmID) {
        try {
            vmid_ = vmID + "";
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws MonitorException, URISyntaxException {
        VmIdentifier vmId = new VmIdentifier(vmid_);
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
        vm_ = monitoredHost.getMonitoredVm(vmId, 0);
    }

    /**
     * 幸存者1容量
     *
     * @return
     */
    public String getSurvival1Capacity() {
        String exper = "sun.gc.generation.0.space.1.capacity";
        return getValueByExper(exper);
    }

    /**
     * 幸存者2容量
     *
     * @return
     */
    public String getSurvival2Capacity() {
        String exper = "sun.gc.generation.0.space.2.capacity";
        return getValueByExper(exper);
    }

    /**
     * 幸存者1已使用
     *
     * @return
     */
    public String getSurvival1Used() {
        String exper = "sun.gc.generation.0.space.1.used";
        return getValueByExper(exper);
    }

    /**
     * 幸存者2已使用
     *
     * @return
     */
    public String getSurvival2Used() {
        String exper = "sun.gc.generation.0.space.2.used";
        return getValueByExper(exper);
    }

    /**
     * 幸存者1最大容量
     *
     * @return
     */
    public String getSurvival1MaxCapacity() {
        String exper = "sun.gc.generation.0.space.1.maxCapacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * 幸存者2最大容量
     *
     * @return
     */
    public String getSurvival2MaxCapacity() {
        String exper = "sun.gc.generation.0.space.2.maxCapacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }


    /**
     * Eden容量
     *
     * @return
     */
    public String getEdenCapacity() {
        String exper = "sun.gc.generation.0.space.0.capacity";
        return getValueByExper(exper);
    }

    /**
     * Eden已使用
     *
     * @return
     */
    public String getEdenUsed() {
        String exper = "sun.gc.generation.0.space.0.used";
        return getValueByExper(exper);
    }

    /**
     * eden最大容量
     *
     * @return
     */
    public String getEdenMaxCapacity() {
        String exper = "sun.gc.generation.0.space.0.maxCapacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * Old容量
     *
     * @return
     */
    public String getOldCapacity() {
        String exper = "sun.gc.generation.1.space.0.capacity";
        return getValueByExper(exper);
    }

    /**
     * Old已使用
     *
     * @return
     */
    public String getOldUsed() {
        String exper = "sun.gc.generation.1.space.0.used";
        return getValueByExper(exper);
    }

    /**
     * old最大容量
     *
     * @return
     */
    public String getOldMaxCapacity() {
        String exper = "sun.gc.generation.1.space.0.maxCapacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * Perm容量
     *
     * @return
     */
    public String getPermCapacity() {
        String exper = "sun.gc.generation.2.space.0.capacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * Perm已使用
     *
     * @return
     */
    public String getPermUsed() {
        String exper = "sun.gc.generation.2.space.0.used";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * Perm最大容量
     *
     * @return
     */
    public String getPermMaxCapacity() {
        String exper = "sun.gc.generation.2.space.0.maxCapacity";
        Identifier id = new Identifier(exper);
        return evaluate(id).toString();
    }

    /**
     * Meta容量
     *
     * @return
     */
    public String getMetaCapacity() {
        String exper = "sun.gc.metaspace.capacity";
        return getValueByExper(exper);
    }

    /**
     * Meta已使用
     *
     * @return
     */
    public String getMetaUsed() {
        String exper = "sun.gc.metaspace.used";
        return getValueByExper(exper);
    }

    /**
     * Meta最大容量
     *
     * @return
     */
    public String getMetaMaxCapacity() {
        String exper = "sun.gc.metaspace.maxCapacity";
        return getValueByExper(exper);
    }

    private String getValueByExper(String exper) {
        Identifier id = new Identifier(exper);
        Object obj = ((Identifier) evaluate(id)).getValue();
        PerfLongMonitor monitor = (PerfLongMonitor) obj;
        return monitor.getValue().toString();
    }

    private Object evaluate(Identifier id) {

        if (id.isResolved()) {
            return id;
        }
        Monitor m = null;
        try {
            m = vm_.findByName(id.getName());
        } catch (MonitorException e) {
            e.printStackTrace();
        }
        if (m == null) {
            return new Literal(new Double(Double.NaN));
        }
        if (m.getVariability() == Variability.CONSTANT) {
            return new Literal(m.getValue());
        }
        id.setValue(m);
        return id;

    }
}
