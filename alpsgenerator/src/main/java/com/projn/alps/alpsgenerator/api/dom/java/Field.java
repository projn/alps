package com.projn.alps.alpsgenerator.api.dom.java;

import com.projn.alps.alpsgenerator.api.dom.OutputUtilities;

/**
 * @author Jeff Butler
 */
public class Field extends AbstractJavaElement {
    private FullyQualifiedJavaType type;
    private String name;
    private String initializationString;
    private boolean isTransient;
    private boolean isVolatile;

    /**
     *
     */
    public Field() {
        // use a default name to avoid NPE
        this("foo", FullyQualifiedJavaType.getIntInstance());
    }

    /**
     * field
     *
     * @param name :
     * @param type :
     */
    public Field(String name, FullyQualifiedJavaType type) {
        super();
        this.name = name;
        this.type = type;
    }

    /**
     * field
     *
     * @param field :
     */
    public Field(Field field) {
        super(field);
        this.type = field.type;
        this.name = field.name;
        this.initializationString = field.initializationString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FullyQualifiedJavaType getType() {
        return type;
    }

    public void setType(FullyQualifiedJavaType type) {
        this.type = type;
    }

    public String getInitializationString() {
        return initializationString;
    }

    public void setInitializationString(String initializationString) {
        this.initializationString = initializationString;
    }

    /**
     * get formatted content
     *
     * @param indentLevel     :
     * @param compilationUnit :
     * @return String :
     */
    public String getFormattedContent(int indentLevel, ICompilationUnit compilationUnit) {
        StringBuilder sb = new StringBuilder();

        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);

        OutputUtilities.javaIndent(sb, indentLevel);
        sb.append(getVisibility().getValue());

        if (isStatic()) {
            sb.append("static ");
        }

        if (isFinal()) {
            sb.append("final ");
        }

        if (isTransient()) {
            sb.append("transient ");
        }

        if (isVolatile()) {
            sb.append("volatile ");
        }

        sb.append(JavaDomUtils.calculateTypeName(compilationUnit, type));

        sb.append(' ');
        sb.append(name);

        if (initializationString != null && initializationString.length() > 0) {
            sb.append(" = ");
            sb.append(initializationString);
        }

        sb.append(';');

        return sb.toString();
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setVolatile(boolean isVolatile) {
        this.isVolatile = isVolatile;
    }
}
