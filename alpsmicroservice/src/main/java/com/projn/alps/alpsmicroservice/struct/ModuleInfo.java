package com.projn.alps.alpsmicroservice.struct;

/**
 * module info
 *
 * @author : sunyuecheng
 */
public class ModuleInfo {
    private String jarPath = null;
    private String configPath = null;

    /**
     * module info
     */
    public ModuleInfo() {
    }

    /**
     * module info
     *
     * @param jarPath    :
     * @param configPath :
     */
    public ModuleInfo(String jarPath, String configPath) {
        this.jarPath = jarPath;
        this.configPath = configPath;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}