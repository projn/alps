package com.projn.alps.alpsgenerator.api.dom.java;

import com.projn.alps.alpsgenerator.api.dom.OutputUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class AbstractJavaElement.
 *
 * @author Jeff Butler
 */
public abstract class AbstractJavaElement {
    
    /** The java doc lines. */
    private List<String> javaDocLines;

    /** The visibility. */
    private JavaVisibility visibility = JavaVisibility.DEFAULT;

    /** The is static. */
    private boolean isStatic;

    /** The is final. */
    private boolean isFinal;

    /** The annotations. */
    private List<String> annotations;

    /**
     * Instantiates a new java element.
     */
    public AbstractJavaElement() {
        super();
        javaDocLines = new ArrayList<String>();
        annotations = new ArrayList<String>();
    }
    
    /**
     * Copy Constructor.
     *
     * @param original
     *            the original
     */
    public AbstractJavaElement(AbstractJavaElement original) {
        this();
        this.annotations.addAll(original.annotations);
        this.isFinal = original.isFinal;
        this.isStatic = original.isStatic;
        this.javaDocLines.addAll(original.javaDocLines);
        this.visibility = original.visibility;
    }

    /**
     * Gets the java doc lines.
     *
     * @return Returns the javaDocLines.
     */
    public List<String> getJavaDocLines() {
        return javaDocLines;
    }

    /**
     * Adds the java doc line.
     *
     * @param javaDocLine
     *            the java doc line
     */
    public void addJavaDocLine(String javaDocLine) {
        javaDocLines.add(javaDocLine);
    }

    /**
     * Gets the annotations.
     *
     * @return the annotations
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     * Adds the annotation.
     *
     * @param annotation
     *            the annotation
     */
    public void addAnnotation(String annotation) {
        annotations.add(annotation);
    }

    /**
     * Gets the visibility.
     *
     * @return Returns the visibility.
     */
    public JavaVisibility getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility.
     *
     * @param visibility
     *            The visibility to set.
     */
    public void setVisibility(JavaVisibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Adds the suppress type warnings annotation.
     */
    public void addSuppressTypeWarningsAnnotation() {
        addAnnotation("@SuppressWarnings(\"unchecked\")");
    }

    /**
     * Adds the formatted javadoc.
     *
     * @param sb
     *            the sb
     * @param indentLevel
     *            the indent level
     */
    public void addFormattedJavadoc(StringBuilder sb, int indentLevel) {
        for (String javaDocLine : javaDocLines) {
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append(javaDocLine);
            OutputUtilities.newLine(sb);
        }
    }

    /**
     * Adds the formatted annotations.
     *
     * @param sb
     *            the sb
     * @param indentLevel
     *            the indent level
     */
    public void addFormattedAnnotations(StringBuilder sb, int indentLevel) {
        for (String annotation : annotations) {
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append(annotation);
            OutputUtilities.newLine(sb);
        }
    }

    /**
     * Checks if is final.
     *
     * @return true, if is final
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Sets the final.
     *
     * @param isFinal
     *            the new final
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Checks if is static.
     *
     * @return true, if is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Sets the static.
     *
     * @param isStatic
     *            the new static
     */
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}
