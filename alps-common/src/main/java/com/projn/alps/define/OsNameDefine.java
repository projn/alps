package com.projn.alps.define;

/**
 * os name define
 *
 * @author : sunyuecheng
 */
public enum OsNameDefine {

    /**
     * centos
     */
    OS_TYPE_CENTOS(0, "centos"),

    /**
     * ubuntu
     */
    OS_TYPE_UBUNTU(1, "ubuntu"),

    /**
     * debian
     */
    OS_TYPE_DEBIAN(2, "debian"),

    /**
     * suse
     */
    OS_TYPE_SUSE(3, "suse"),

    /**
     * opensuse
     */
    OS_TYPE_OPENSUSE(4, "opensuse"),

    /**
     * euler
     */
    OS_TYPE_EULER(5, "euler"),

    /**
     * euleros
     */
    OS_TYPE_EULEROS(6, "euleros"),

    /**
     * redhat
     */
    OS_TYPE_REDHAT(7, "redhat"),

    /**
     * fedora
     */
    OS_TYPE_FEDORA(8, "fedora"),

    /**
     * unknown
     */
    OS_TYPE_UNKNOWN(100, "unknown");

    private int index;
    private String osName;

    OsNameDefine(int index, String osName) {
        this.index = index;
        this.osName = osName;
    }

    public int getIndex() {
        return index;
    }

    public String getOsName() {
        return osName;
    }

    /**
     * get os name
     *
     * @param index :
     * @return : String
     */
    public static String getOsName(int index) {
        for (OsNameDefine osNameDefine : OsNameDefine.values()) {
            if (osNameDefine.getIndex() == index) {
                return osNameDefine.getOsName();
            }
        }
        return OS_TYPE_UNKNOWN.getOsName();
    }
}
