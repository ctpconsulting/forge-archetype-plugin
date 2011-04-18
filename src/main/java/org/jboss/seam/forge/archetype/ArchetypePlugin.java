package org.jboss.seam.forge.archetype;

import groovy.lang.GroovyShell;

import java.io.IOException;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.codehaus.plexus.util.IOUtil;
import org.jboss.seam.forge.archetype.model.Archetype;
import org.jboss.seam.forge.archetype.qualifier.Initialized;
import org.jboss.seam.forge.resources.FileResource;
import org.jboss.seam.forge.resources.Resource;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.Plugin;


@Alias("archetype")
public class ArchetypePlugin implements Plugin {
    
    private static final String SCRIPT_PREFIX = 
            "import org.jboss.seam.forge.archetype.model.Archetype;\n" +
            "def archetype = new Archetype(beanManager);\n" +
            "archetype.setName(''{0}'');\n";
    
    private static final String SCRIPT_POSTFIX = 
            "\nreturn archetype;";

    @Inject @Initialized
    private GroovyShell groovyShell;
    
    @Inject
    private Shell shell;
    
    @Command
    public void create(
            @Option(required = true) final Resource<?> resource,
            @Option(name = "named", required = true) String name) {
        if (resource instanceof FileResource) {
            try {
                String script = prefix(name) + IOUtil.toString(resource.getResourceInputStream()) + SCRIPT_POSTFIX;
                Archetype archetype = (Archetype) groovyShell.evaluate(script);
                if (!archetype.isCreated()) {
                    archetype.create(shell);
                }
            } catch (IOException e) {
                throw new RuntimeException("error executing script from file: " + resource.getName());
            }
        } else {
            throw new RuntimeException("resource type not an executable script: " + resource.getClass().getName());
        }
    }
    
    private String prefix(String name) {
        return MessageFormat.format(SCRIPT_PREFIX, name);
    }

}
