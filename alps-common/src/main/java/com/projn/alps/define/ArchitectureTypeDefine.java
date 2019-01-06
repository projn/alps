package com.projn.alps.define;

/**
 * architecture type define
 *
 * @author : sunyuecheng
 */
public enum ArchitectureTypeDefine {

    /**
     * x86 architecture
     */
    X86(0, "x86"),

    /**
     * x64 architecture
     */
    X64(1, "x64"),

    /**
     * amd64 architecture
     */
    AMD64(2, "amd64");
    private int index;

    private String name;

    ArchitectureTypeDefine(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    /**
     * get architecture type
     *
     * @param name :
     * @return : ArchitectureTypeDefine
     */
    public static ArchitectureTypeDefine getArchitectureType(String name) {
        for (ArchitectureTypeDefine status : ArchitectureTypeDefine.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }

        return null;
    }
}
