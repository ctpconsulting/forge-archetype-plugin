package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.PackagingFacet;
import org.jboss.seam.forge.project.packaging.PackagingType;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

public class Archetype extends Module {
    
    private static final String ROOT_CALL = "call";
    
    private boolean created;
    private List<Module> modules = new LinkedList<Module>();

    public Archetype(BeanManager beanManager) {
        super(beanManager);
    }
    
    public Archetype(BeanManager beanManager, String name) {
        super(beanManager, name);
    }

    @Override
    public Object invokeMethod(String methodName, Object obj) {
        Object[] args = asArray(obj);
        if (MODULE.equals(methodName)) {
            Module module = new Module(beanManager, name);
            modules.add(module);
            return module.invokeMethod(methodName, obj);
        }
        if (!ROOT_CALL.equals(methodName)) {
            PluginCommand command = new PluginCommand(beanManager, methodName);
            plugins.add(command);
            return command.invokeMethod(methodName, obj);
        }
        if (args[0] instanceof Closure) {
            invokeClosure((Closure) args[0]);
        }
        return this;
    }
    
    public void create() {
        Shell shell = resolveShell();
        create(shell);
    }
    
    @Override
    public void create(Shell shell) {
        ShellMessages.info(shell, "Creating new project " + name);
        shell.execute("new-project --named " + name);
        runPlugins(shell);
        runModules(shell);
        postProcess(shell);
        created = true;
    }

    protected void runModules(Shell shell) {
        for (Module module : modules) {
            module.create(this, shell);
            shell.execute("cd ..");
        }
    }
    
    protected void postProcess(Shell shell) {
        if (modules.size() > 0) {
            ShellMessages.info(shell, "Converting parent project to POM packaging");
            Project project = resolveProject();
            PackagingFacet facet = project.getFacet(PackagingFacet.class);
            facet.setPackagingType(PackagingType.BASIC);
            ShellMessages.info(shell, "Removing unnecessary folders");
            shell.execute("rm -r src/main/java src/main/resources src/test");
        }
    }

    public boolean isCreated() {
        return created;
    }

}
