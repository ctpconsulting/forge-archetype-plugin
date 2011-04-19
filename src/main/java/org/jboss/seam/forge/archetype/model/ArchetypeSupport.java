package org.jboss.seam.forge.archetype.model;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.util.BeanManagerUtils;

public abstract class ArchetypeSupport extends GroovyObjectSupport {
    
    protected final BeanManager beanManager;
    
    public ArchetypeSupport(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    protected void invokeClosure(Closure closure) {
        closure.setDelegate(this);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }
    
    protected Object[] asArray(Object obj) {
        Object[] args = obj.getClass().isArray() ? (Object[]) obj : new Object[] { obj };
        return args;
    }
    
    protected Project resolveProject() {
        return BeanManagerUtils.getContextualInstance(beanManager, Project.class);
    }
    
    protected Shell resolveShell() {
        return BeanManagerUtils.getContextualInstance(beanManager, Shell.class);
    }
    
}
