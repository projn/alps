package com.projn.sample.alps.module.backend.property;

import org.springframework.stereotype.Component;

/**
 * cgs module properties
 *
 * @author : s00441405
 */
@Component("BackendModuleProperties")
public class BackendModuleProperties {
    private String thirdInterfaceAlarmProgramPath = null;

    public String getThirdInterfaceAlarmProgramPath() {
        return thirdInterfaceAlarmProgramPath;
    }

    public void setThirdInterfaceAlarmProgramPath(String thirdInterfaceAlarmProgramPath) {
        this.thirdInterfaceAlarmProgramPath = thirdInterfaceAlarmProgramPath;
    }
}
