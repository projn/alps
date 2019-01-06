package com.projn.alps.define;

/**
 * os type define
 *
 * @author : sunyuecheng
 */
public enum OsTypeDefine {
    /**
     * linux
     */
    LINUX(0, "linux"),

    /**
     * windows
     */
    WINDOWS(1, "windows");

    private int index;

    private String name;

    OsTypeDefine(int index, String name) {
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
     * get os type
     *
     * @param name :
     * @return : OsTypeDefine
     */
    public static OsTypeDefine getOsType(String name) {
        for (OsTypeDefine status : OsTypeDefine.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }

        return null;
    }

}
