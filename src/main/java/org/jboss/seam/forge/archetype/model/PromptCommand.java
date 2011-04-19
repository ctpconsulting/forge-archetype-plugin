package org.jboss.seam.forge.archetype.model;

import org.codehaus.plexus.util.StringUtils;
import org.jboss.seam.forge.shell.Shell;

public class PromptCommand implements Executable {
    
    private final String key;
    private final String description;
    private final String defaultVal;
    
    public PromptCommand(Object[] args) {
        if (args.length < 2)
            throw new RuntimeException("Prompt must be set up with 2 arguments");
        this.key = (String) args[0];
        this.description = (String) args[1];
        if (args.length > 2)
            this.defaultVal = (String) args[2];
        else
            this.defaultVal = null;
    }

    @Override
    public void execute(Shell shell, ArchetypeContext context) {
        String prompt = description + (defaultVal != null ? " [" + defaultVal + "]" : "");
        String value = shell.prompt(prompt);
        if (StringUtils.isEmpty(value) && StringUtils.isNotEmpty(defaultVal)) {
            context.put(key, defaultVal);
        } else {
            context.put(key, value);
        }
    }

}
