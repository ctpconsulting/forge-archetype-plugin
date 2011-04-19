package org.jboss.seam.forge.archetype.model;

import org.jboss.seam.forge.shell.Shell;

public interface Executable {

    void execute(Shell shell, ArchetypeContext context);

}
