package com.projn.alps.alpsgenerator.api.dom.java;

import com.projn.alps.alpsgenerator.api.dom.OutputUtilities;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * The Class Interface.
 *
 * @author Jeff Butler
 */
public class Interface extends AbstractJavaElement implements ICompilationUnit {

    /**
     * The imported types.
     */
    private Set<FullyQualifiedJavaType> importedTypes;

    /**
     * The static imports.
     */
    private Set<String> staticImports;

    /**
     * The type.
     */
    private FullyQualifiedJavaType type;

    /**
     * The super interface types.
     */
    private Set<FullyQualifiedJavaType> superInterfaceTypes;

    /**
     * The methods.
     */
    private List<Method> methods;

    /**
     * The file comment lines.
     */
    private List<String> fileCommentLines;

    /**
     * Instantiates a new interface.
     *
     * @param type the type
     */
    public Interface(FullyQualifiedJavaType type) {
        super();
        this.type = type;
        superInterfaceTypes = new LinkedHashSet<FullyQualifiedJavaType>();
        methods = new ArrayList<Method>();
        importedTypes = new TreeSet<FullyQualifiedJavaType>();
        fileCommentLines = new ArrayList<String>();
        staticImports = new TreeSet<String>();
    }

    /**
     * Instantiates a new interface.
     *
     * @param type the type
     */
    public Interface(String type) {
        this(new FullyQualifiedJavaType(type));
    }

    /**
     * get imported types
     *
     * @return Set<FullyQualifiedJavaType> :
     */
    @Override
    public Set<FullyQualifiedJavaType> getImportedTypes() {
        return Collections.unmodifiableSet(importedTypes);
    }

    /**
     * add imported type
     *
     * @param importedType :
     */
    @Override
    public void addImportedType(FullyQualifiedJavaType importedType) {
        if (importedType.isExplicitlyImported()
                && !importedType.getPackageName().equals(type.getPackageName())) {
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

        for (String commentLine : fileCommentLines) {
            sb.append(commentLine);
            OutputUtilities.newLine(sb);
        }

        if (StringUtils.isEmpty(getType().getPackageName())) {
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

        int indentLevel = 0;

        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);

        sb.append(getVisibility().getValue());

        if (isStatic()) {
            sb.append("static ");
        }

        if (isFinal()) {
            sb.append("final ");
        }

        sb.append("interface ");
        sb.append(getType().getShortName());

        if (getSuperInterfaceTypes().size() > 0) {
            sb.append(" extends ");

            boolean comma = false;
            for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }

                sb.append(JavaDomUtils.calculateTypeName(this, fqjt));
            }
        }

        sb.append(" {");
        indentLevel++;

        Iterator<Method> mtdIter = getMethods().iterator();
        while (mtdIter.hasNext()) {
            OutputUtilities.newLine(sb);
            Method method = mtdIter.next();
            sb.append(method.getFormattedContent(indentLevel, true, this));
            if (mtdIter.hasNext()) {
                OutputUtilities.newLine(sb);
            }
        }

        indentLevel--;
        OutputUtilities.newLine(sb);
        OutputUtilities.javaIndent(sb, indentLevel);
        sb.append('}');

        return sb.toString();
    }

    /**
     * Adds the super interface.
     *
     * @param superInterface the super interface
     */
    public void addSuperInterface(FullyQualifiedJavaType superInterface) {
        superInterfaceTypes.add(superInterface);
    }

    /**
     * Gets the methods.
     *
     * @return Returns the methods.
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * Adds the method.
     *
     * @param method the method
     */
    public void addMethod(Method method) {
        methods.add(method);
    }

    /**
     * Gets the type.
     *
     * @return Returns the type.
     */
    @Override
    public FullyQualifiedJavaType getType() {
        return type;
    }

    /**
     * get super class
     *
     * @return FullyQualifiedJavaType :
     */
    @Override
    public FullyQualifiedJavaType getSuperClass() {
        // interfaces do not have superclasses
        return null;
    }

    /**
     * get super interface types
     *
     * @return Set<FullyQualifiedJavaType> :
     */
    @Override
    public Set<FullyQualifiedJavaType> getSuperInterfaceTypes() {
        return superInterfaceTypes;
    }

    /**
     * is java interface
     *
     * @return boolean :
     */
    @Override
    public boolean isJavaInterface() {
        return true;
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
