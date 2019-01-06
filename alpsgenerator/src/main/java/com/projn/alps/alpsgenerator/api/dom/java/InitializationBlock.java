package com.projn.alps.alpsgenerator.api.dom.java;

import com.projn.alps.alpsgenerator.api.dom.OutputUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * initialization block
 *
 * @author : sunyuecheng
 */
public class InitializationBlock {

    private boolean isStatic;
    private List<String> bodyLines;
    private List<String> javaDocLines;

    /**
     * initialization block
     */
    public InitializationBlock() {
        this(false);
    }

    /**
     * initialization block
     *
     * @param isStatic :
     */
    public InitializationBlock(boolean isStatic) {
        this.isStatic = isStatic;
        bodyLines = new ArrayList<String>();
        javaDocLines = new ArrayList<String>();
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public List<String> getBodyLines() {
        return bodyLines;
    }

    /**
     * add body line
     *
     * @param line :
     */
    public void addBodyLine(String line) {
        bodyLines.add(line);
    }

    /**
     * add body line
     *
     * @param index :
     * @param line  :
     */
    public void addBodyLine(int index, String line) {
        bodyLines.add(index, line);
    }

    /**
     * add body lines
     *
     * @param lines :
     */
    public void addBodyLines(Collection<String> lines) {
        bodyLines.addAll(lines);
    }

    /**
     * add body lines
     *
     * @param index :
     * @param lines :
     */
    public void addBodyLines(int index, Collection<String> lines) {
        bodyLines.addAll(index, lines);
    }

    public List<String> getJavaDocLines() {
        return javaDocLines;
    }

    /**
     * add java doc line
     *
     * @param javaDocLine :
     */
    public void addJavaDocLine(String javaDocLine) {
        javaDocLines.add(javaDocLine);
    }

    /**
     * get formatted content
     *
     * @param indentLevel :
     * @return String :
     */
    public String getFormattedContent(int indentLevel) {
        StringBuilder sb = new StringBuilder();

        for (String javaDocLine : javaDocLines) {
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append(javaDocLine);
            OutputUtilities.newLine(sb);
        }

        OutputUtilities.javaIndent(sb, indentLevel);

        if (isStatic) {
            sb.append("static ");
        }

        sb.append('{');
        indentLevel++;

        ListIterator<String> listIter = bodyLines.listIterator();
        while (listIter.hasNext()) {
            String line = listIter.next();
            if (line.startsWith("}")) {
                indentLevel--;
            }

            OutputUtilities.newLine(sb);
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append(line);

            if ((line.endsWith("{") && !line.startsWith("switch"))
                    || line.endsWith(":")) {
                indentLevel++;
            }

            if (line.startsWith("break")) {
                // if the next line is '}', then don't outdent
                if (listIter.hasNext()) {
                    String nextLine = listIter.next();
                    if (nextLine.startsWith("}")) {
                        indentLevel++;
                    }

                    // set back to the previous element
                    listIter.previous();
                }
                indentLevel--;
            }
        }

        indentLevel--;
        OutputUtilities.newLine(sb);
        OutputUtilities.javaIndent(sb, indentLevel);
        sb.append('}');

        return sb.toString();
    }
}
