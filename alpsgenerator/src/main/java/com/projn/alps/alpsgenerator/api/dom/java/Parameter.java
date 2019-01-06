package com.projn.alps.alpsgenerator.api.dom.java;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Butler
 */
public class Parameter {
    private String name;
    private FullyQualifiedJavaType type;
    private boolean isVarargs;

    private List<String> annotations;

    /**
     * parameter
     *
     * @param type      :
     * @param name      :
     * @param isVarargs :
     */
    public Parameter(FullyQualifiedJavaType type, String name, boolean isVarargs) {
        super();
        this.name = name;
        this.type = type;
        this.isVarargs = isVarargs;
        annotations = new ArrayList<String>();
    }

    /**
     * parameter
     *
     * @param type :
     * @param name :
     */
    public Parameter(FullyQualifiedJavaType type, String name) {
        this(type, name, false);
    }

    /**
     * parameter
     *
     * @param type       :
     * @param name       :
     * @param annotation :
     */
    public Parameter(FullyQualifiedJavaType type, String name, String annotation) {
        this(type, name, false);
        addAnnotation(annotation);
    }

    /**
     * parameter
     *
     * @param type       :
     * @param name       :
     * @param annotation :
     * @param isVarargs  :
     */
    public Parameter(FullyQualifiedJavaType type, String name, String annotation, boolean isVarargs) {
        this(type, name, isVarargs);
        addAnnotation(annotation);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public FullyQualifiedJavaType getType() {
        return type;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * add annotation
     *
     * @param annotation :
     */
    public void addAnnotation(String annotation) {
        annotations.add(annotation);
    }

    /**
     * get formatted content
     *
     * @param compilationUnit :
     * @return String :
     */
    public String getFormattedContent(ICompilationUnit compilationUnit) {
        StringBuilder sb = new StringBuilder();

        for (String annotation : annotations) {
            sb.append(annotation);
            sb.append(' ');
        }

        sb.append(JavaDomUtils.calculateTypeName(compilationUnit, type));

        sb.append(' ');
        if (isVarargs) {
            sb.append("... ");
        }
        sb.append(name);

        return sb.toString();
    }

    /**
     * to string
     *
     * @param :
     * @return String :
     */
    @Override
    public String toString() {
        return getFormattedContent(null);
    }

    public boolean isVarargs() {
        return isVarargs;
    }
}
