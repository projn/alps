package com.projn.alps.alpsgenerator.api;

import com.projn.alps.alpsgenerator.api.dom.java.ICompilationUnit;

/**
 * The Class AbstractGeneratedJavaFile.
 *
 * @author Jeff Butler
 */
public class AbstractGeneratedJavaFile extends AbstractGeneratedFile {

    /**
     * The compilation unit.
     */
    private ICompilationUnit compilationUnit;

    /**
     * The file encoding.
     */
    private String fileEncoding;

    /**
     * Default constructor.
     *
     * @param compilationUnit the compilation unit
     * @param targetProject   the target project
     * @param fileEncoding    the file encoding
     */
    public AbstractGeneratedJavaFile(ICompilationUnit compilationUnit,
                                     String targetProject,
                                     String fileEncoding) {
        super(targetProject);
        this.compilationUnit = compilationUnit;
        this.fileEncoding = fileEncoding;
    }

    /**
     * get formatted content
     *
     * @param :
     * @return String :
     */
    @Override
    public String getFormattedContent() {
        return compilationUnit.getFormattedContent();
    }

    /**
     * get file name
     *
     * @param :
     * @return String :
     */
    @Override
    public String getFileName() {
        return compilationUnit.getType().getShortNameWithoutTypeArguments() + ".java";
    }

    /**
     * get target package
     *
     * @param :
     * @return String :
     */
    @Override
    public String getTargetPackage() {
        return compilationUnit.getType().getPackageName();
    }

    /**
     * This method is required by the Eclipse Java merger. If you are not
     * running in Eclipse, or some other system that implements the Java merge
     * function, you may return null from this method.
     *
     * @return the CompilationUnit associated with this file, or null if the
     * file is not mergeable.
     */
    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * A Java file is mergeable if the getCompilationUnit() method returns a valid compilation unit.
     *
     * @return true, if is mergeable
     */
    @Override
    public boolean isMergeable() {
        return true;
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    public String getFileEncoding() {
        return fileEncoding;
    }
}
