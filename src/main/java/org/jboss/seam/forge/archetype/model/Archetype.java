package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

public class Archetype extends ArchetypeSupport {
    
    private static final String ROOT_CALL = "call";
    
    private String name;
    private boolean created;
    private List<PluginCommand> plugins = new LinkedList<PluginCommand>();
    
    public Archetype(BeanManager beanManager) {
        super(beanManager);
    }

    @Override
    public Object invokeMethod(String name, Object obj) {
        Object[] args = asArray(obj);
        if (!ROOT_CALL.equals(name)) {
            PluginCommand command = new PluginCommand(beanManager, name);
            plugins.add(command);
            return command.invokeMethod(name, obj);
        }
        if (args[0] instanceof Closure) {
            invokeClosure((Closure) args[0]);
        }
        return this;
    }
    
    public void create(Shell shell) {
        ShellMessages.info(shell, "Creating new project " + name);
        shell.execute("new-project --named " + name);
        for (PluginCommand plugin : plugins) {
            plugin.run(shell);
        }
        created = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCreated() {
        return created;
    }

}
