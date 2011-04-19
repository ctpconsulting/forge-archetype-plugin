package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

public class Module extends ArchetypeSupport {
    
    private static final String EXTENSION = "extension";
    protected static final String MODULE = "module";
    protected static final String PROMPT = "prompt";
    
    private String extension;
    
    protected String name;
    protected List<Executable> plugins = new LinkedList<Executable>();
    
    public Module(BeanManager beanManager) {
        this(beanManager, null);
    }

    public Module(BeanManager beanManager, String name) {
        super(beanManager);
        this.name = name;
    }

    @Override
    public Object invokeMethod(String methodName, Object obj) {
        Object[] args = asArray(obj);
        if (EXTENSION.equals(methodName)) {
            extension = (String) args[0];
            return this;
        }
        if (PROMPT.equals(methodName)) {
            PromptCommand prompt = new PromptCommand(args);
            plugins.add(prompt);
            return this;
        }
        if (MODULE.equals(methodName)) {
            invokeClosure((Closure) args[0]);
            return this;
        }
        PluginCommand command = new PluginCommand(beanManager, methodName);
        plugins.add(command);
        return command.invokeMethod(methodName, obj);
    }
    
    public Model pom() {
        Project project = resolveProject();
        MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        return facet.getPOM();
    }
    
    public void create() {
        create(resolveShell());
    }
    
    public void create(Shell shell) {
        create(shell, null);
    }

    public void create(Shell shell, ArchetypeContext parentCtx) {
        if (extension == null)
            throw new RuntimeException("Module should have a name extension - please add an 'extension' property.");
        String moduleName = getModuleName();
        ShellMessages.info(shell, "Creating new module " + moduleName);
        
        Project project = resolveProject();
        String basePackage = project.getFacet(JavaSourceFacet.class).getBasePackage();
        String folder = project.getProjectRoot().getChildDirectory(moduleName)
                .getUnderlyingResourceObject().getAbsolutePath();
        
        shell.execute("new-project --named " + moduleName + " --topLevelPackage " + basePackage + " --projectFolder " + folder);
        ArchetypeContext context = new ArchetypeContext(this, parentCtx);
        runPlugins(shell, context);
        
        if (parentCtx != null) {
            ShellMessages.info(shell, "Adding parent POM to " + moduleName);
            MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
            Model pom = facet.getPOM();
            Parent parentPom = new Parent();
            parentPom.setGroupId(basePackage);
            parentPom.setArtifactId(parentCtx.getModule().getName());
            parentPom.setRelativePath("../");
            parentPom.setVersion(pom.getVersion());
            pom.setParent(parentPom);
            facet.setPOM(pom);
        }
    }
    
    public String getModuleName() {
        return name + "-" + extension;
    }
    
    protected void runPlugins(Shell shell, ArchetypeContext context) {
        for (Executable plugin : plugins) {
            plugin.execute(shell, context);
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
