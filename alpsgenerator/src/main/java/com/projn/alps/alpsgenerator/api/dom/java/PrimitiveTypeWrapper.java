package com.projn.alps.alpsgenerator.api.dom.java;

/**
 * @author Jeff Butler
 */
public final class PrimitiveTypeWrapper extends FullyQualifiedJavaType {
    private static PrimitiveTypeWrapper booleanInstance;
    private static PrimitiveTypeWrapper byteInstance;
    private static PrimitiveTypeWrapper characterInstance;
    private static PrimitiveTypeWrapper doubleInstance;
    private static PrimitiveTypeWrapper floatInstance;
    private static PrimitiveTypeWrapper integerInstance;
    private static PrimitiveTypeWrapper longInstance;
    private static PrimitiveTypeWrapper shortInstance;

    private String toPrimitiveMethod;

    /**
     * Use the static getXXXInstance methods to gain access to one of the type
     * wrappers.
     *
     * @param fullyQualifiedName :
     * @param toPrimitiveMethod  :
     */
    private PrimitiveTypeWrapper(String fullyQualifiedName,
                                 String toPrimitiveMethod) {
        super(fullyQualifiedName);
        this.toPrimitiveMethod = toPrimitiveMethod;
    }

    /**
     * get to primitive method
     *
     * @param :
     * @return String :
     */
    public String getToPrimitiveMethod() {
        return toPrimitiveMethod;
    }

    /**
     * get boolean instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getBooleanInstance() {
        if (booleanInstance == null) {
            booleanInstance = new PrimitiveTypeWrapper("java.lang.Boolean",
                    "booleanValue()");
        }

        return booleanInstance;
    }

    /**
     * get byte instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getByteInstance() {
        if (byteInstance == null) {
            byteInstance = new PrimitiveTypeWrapper("java.lang.Byte",
                    "byteValue()");
        }

        return byteInstance;
    }

    /**
     * get character instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getCharacterInstance() {
        if (characterInstance == null) {
            characterInstance = new PrimitiveTypeWrapper("java.lang.Character",
                    "charValue()");
        }

        return characterInstance;
    }

    /**
     * get double instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getDoubleInstance() {
        if (doubleInstance == null) {
            doubleInstance = new PrimitiveTypeWrapper("java.lang.Double",
                    "doubleValue()");
        }

        return doubleInstance;
    }

    /**
     * get float instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getFloatInstance() {
        if (floatInstance == null) {
            floatInstance = new PrimitiveTypeWrapper("java.lang.Float",
                    "floatValue()");
        }

        return floatInstance;
    }

    /**
     * get integer instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getIntegerInstance() {
        if (integerInstance == null) {
            integerInstance = new PrimitiveTypeWrapper("java.lang.Integer",
                    "intValue()");
        }

        return integerInstance;
    }

    /**
     * get long instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getLongInstance() {
        if (longInstance == null) {
            longInstance = new PrimitiveTypeWrapper("java.lang.Long",
                    "longValue()");
        }

        return longInstance;
    }

    /**
     * get short instance
     *
     * @return PrimitiveTypeWrapper :
     */
    public static PrimitiveTypeWrapper getShortInstance() {
        if (shortInstance == null) {
            shortInstance = new PrimitiveTypeWrapper("java.lang.Short",
                    "shortValue()");
        }

        return shortInstance;
    }

    /**
     * equals
     *
     * @param obj :
     * @return boolean :
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FullyQualifiedJavaType)) {
            return false;
        }

        FullyQualifiedJavaType other = (FullyQualifiedJavaType) obj;

        return getFullyQualifiedName().equals(other.getFullyQualifiedName());
    }

    /**
     * hash code
     *
     * @return int :
     */
    @Override
    public int hashCode() {
        return getFullyQualifiedName().hashCode();
    }

}
