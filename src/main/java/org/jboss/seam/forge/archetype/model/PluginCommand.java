package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.forge.archetype.util.Interpolator;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.ShellMessages;

class PluginCommand extends ArchetypeSupport implements Executable {
    
    private List<Command> commands = new LinkedList<Command>();
    
    private final String plugin;
    
    public PluginCommand(BeanManager beanManager, String plugin) {
        super(beanManager);
        this.plugin = plugin;
    }

    @Override
    public Object invokeMethod(String methodName, Object arg) {
        Object[] args = asArray(arg);
        if (methodName.equals("minus")) { // groovy shell interprets - as minus()
            Command keep = commands.get(commands.size() - 2);
            Command discard = commands.get(commands.size() - 1);
            keep.setName(keep.getName() + "-" + discard.getName());
            keep.setArgs(discard.getArgs());
            commands.remove(discard);
            return this;
        }
        if (args[0] instanceof Closure) {
            invokeClosure((Closure) args[0]);
        } else {
            Command command = new Command(methodName, args);
            commands.add(command);
        }
        return this;
    }
    
    @Override
    public Object getProperty(String property) {
        Command context = new Command(property, null);
        commands.add(context);
        return this;
    }

    @Override
    public void execute(Shell shell, ArchetypeContext context) {
        for (Command command : commands) {
            String execute = plugin + " " + command.getName();
            if (command.getArgs() != null && command.getArgs().length > 0) {
                for (int i = 0; i < command.getArgs().length; i++) {
                    execute += " " + Interpolator.interpolate(command.arg(i), context);
                }
            }
            ShellMessages.info(shell, "Running " + execute);
            shell.execute(execute);
        }
    }

}
