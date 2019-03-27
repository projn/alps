package com.projn.alps.alpsmicroservice.struct;

import java.util.List;

/**
 * module info
 *
 * @author : sunyuecheng
 */
public class ModuleInfo {
    private List<String> jarPathList = null;
    private String configPath = null;

    /**
     * module info
     */
    public ModuleInfo() {
    }

    /**
     * module info
     *
     * @param jarPathList    :
     * @param configPath :
     */
    public ModuleInfo(List<String> jarPathList, String configPath) {
        this.jarPathList = jarPathList;
        this.configPath = configPath;
    }

    public List<String> getJarPathList() {
        return jarPathList;
    }

    public void setJarPathList(List<String> jarPathList) {
        this.jarPathList = jarPathList;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}