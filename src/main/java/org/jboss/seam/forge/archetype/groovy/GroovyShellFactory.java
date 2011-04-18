package org.jboss.seam.forge.archetype.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.seam.forge.archetype.qualifier.Initialized;
import org.jboss.seam.forge.shell.Shell;

public class GroovyShellFactory {
    
    @Inject
    private Shell shell;
    
    @Inject
    private BeanManager beanManager;

    @Produces @Initialized
    public GroovyShell produce() {
        Binding binding = new Binding();
        binding.setVariable("shell", shell);
        binding.setVariable("beanManager", beanManager);
        GroovyShell groovyShell = new GroovyShell(binding);
        return groovyShell;
    }

}
