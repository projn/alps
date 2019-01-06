package com.projn.alps.alpsgenerator.api.dom.java;

import com.projn.alps.alpsgenerator.api.dom.OutputUtilities;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * The Class TopLevelClass.
 *
 * @author Jeff Butler
 */
public class TopLevelClass extends InnerClass implements ICompilationUnit {

    /**
     * The imported types.
     */
    private Set<FullyQualifiedJavaType> importedTypes;

    /**
     * The static imports.
     */
    private Set<String> staticImports;

    /**
     * The file comment lines.
     */
    private List<String> fileCommentLines;

    /**
     * Instantiates a new top level class.
     *
     * @param type the type
     */
    public TopLevelClass(FullyQualifiedJavaType type) {
        super(type);
        importedTypes = new TreeSet<FullyQualifiedJavaType>();
        fileCommentLines = new ArrayList<String>();
        staticImports = new TreeSet<String>();
    }

    /**
     * Instantiates a new top level class.
     *
     * @param typeName the type name
     */
    public TopLevelClass(String typeName) {
        this(new FullyQualifiedJavaType(typeName));
    }

    /**
     * Gets the imported types.
     *
     * @return Returns the importedTypes.
     */
    @Override
    public Set<FullyQualifiedJavaType> getImportedTypes() {
        return Collections.unmodifiableSet(importedTypes);
    }

    /**
     * Adds the imported type.
     *
     * @param importedType the imported type
     */
    public void addImportedType(String importedType) {
        addImportedType(new FullyQualifiedJavaType(importedType));
    }

    /**
     * add imported type
     *
     * @param importedType :
     */
    @Override
    public void addImportedType(FullyQualifiedJavaType importedType) {
        if (importedType != null
                && importedType.isExplicitlyImported()
                && !importedType.getPackageName().equals(
                getType().getPackageName())
                && !importedType.getShortName().equals(getType().getShortName())) {
            importedTypes.add(importedType);
        }
    }

    /**
     * get formatted content
     *
     * @return String :
     */
    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();

        for (String fileCommentLine : fileCommentLines) {
            sb.append(fileCommentLine);
            OutputUtilities.newLine(sb);
        }

        if (!StringUtils.isEmpty(getType().getPackageName())) {
            sb.append("package ");
            sb.append(getType().getPackageName());
            sb.append(';');
            OutputUtilities.newLine(sb);
            OutputUtilities.newLine(sb);
        }

        for (String staticImport : staticImports) {
            sb.append("import static ");
            sb.append(staticImport);
            sb.append(';');
            OutputUtilities.newLine(sb);
        }

        if (staticImports.size() > 0) {
            OutputUtilities.newLine(sb);
        }

        Set<String> importStrings = OutputUtilities.calculateImports(importedTypes);
        for (String importString : importStrings) {
            sb.append(importString);
            OutputUtilities.newLine(sb);
        }

        if (importStrings.size() > 0) {
            OutputUtilities.newLine(sb);
        }

        sb.append(super.getFormattedContent(0, this));

        return sb.toString();
    }

    /**
     * is java interface
     *
     * @return boolean :
     */
    @Override
    public boolean isJavaInterface() {
        return false;
    }

    /**
     * is java enumeration
     *
     * @return boolean :
     */
    @Override
    public boolean isJavaEnumeration() {
        return false;
    }

    /**
     * add file comment line
     *
     * @param commentLine :
     */
    @Override
    public void addFileCommentLine(String commentLine) {
        fileCommentLines.add(commentLine);
    }

    /**
     * get file comment lines
     *
     * @return List<String> :
     */
    @Override
    public List<String> getFileCommentLines() {
        return fileCommentLines;
    }

    /**
     * add imported types
     *
     * @param importedTypes :
     */
    @Override
    public void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes) {
        this.importedTypes.addAll(importedTypes);
    }

    /**
     * get static imports
     *
     * @return Set<String> :
     */
    @Override
    public Set<String> getStaticImports() {
        return staticImports;
    }

    /**
     * add static import
     *
     * @param staticImport :
     */
    @Override
    public void addStaticImport(String staticImport) {
        staticImports.add(staticImport);
    }

    /**
     * add static imports
     *
     * @param staticImports :
     */
    @Override
    public void addStaticImports(Set<String> staticImports) {
        this.staticImports.addAll(staticImports);
    }
}
