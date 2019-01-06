package com.projn.alps.alpsgenerator.api.dom.java;

/**
 * Typesafe enum of possible Java visibility settings
 *
 * @author Jeff Butler
 */
public enum JavaVisibility {

    /**
     * public
     */
    PUBLIC("public "),

    /**
     * private
     */
    PRIVATE("private "),

    /**
     * protected
     */
    PROTECTED("protected "),

    /**
     * default
     */
    DEFAULT("");

    private String value;

    JavaVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
