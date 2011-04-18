package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

class PluginCommand extends ArchetypeSupport {
    
    private List<Command> contexts = new LinkedList<Command>();
    
    private final String plugin;
    
    public PluginCommand(BeanManager beanManager, String plugin) {
        super(beanManager);
        this.plugin = plugin;
    }

    @Override
    public Object invokeMethod(String methodName, Object arg) {
        Object[] args = asArray(arg);
        if (methodName.equals("minus")) {
            Command keep = contexts.get(contexts.size() - 2);
            Command discard = contexts.get(contexts.size() - 1);
            keep.setName(keep.getName() + "-" + discard.getName());
            keep.setArgs(discard.getArgs());
            contexts.remove(discard);
            return this;
        }
        if (args[0] instanceof Closure) {
            invokeClosure((Closure) args[0]);
        } else {
            Command context = new Command(methodName, args);
            contexts.add(context);
        }
        return this;
    }
    
    @Override
    public Object getProperty(String property) {
        Command context = new Command(property, null);
        contexts.add(context);
        return this;
    }

    public void run(Shell shell) {
        for (Command context : contexts) {
            String execute = plugin + " " + context.getName();
            if (context.getArgs() != null && context.getArgs().length > 0) {
                for (int i = 0; i < context.getArgs().length; i++) {
                    execute += " " + context.arg(i);
                }
            }
            ShellMessages.info(shell, "Running " + execute);
            shell.execute(execute);
        }
    }

}
