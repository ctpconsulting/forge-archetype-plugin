package org.jboss.seam.forge.archetype.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Model;

public class ArchetypeContext {

    private Map<String, Object> map = new HashMap<String, Object>();
    private Module module;
    private ArchetypeContext parent;
    
    public ArchetypeContext(Module module) {
        this(module, null);
    }
    
    public ArchetypeContext(Module module, ArchetypeContext parent) {
        this.module = module;
        this.parent = parent;
        Model pom = module.pom();
        map.put("groupId", pom.getGroupId());
        map.put("version", pom.getVersion());
        map.put("artifactId", pom.getArtifactId());
    }
    
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Object get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        if (key.startsWith("parent.") && parent != null) {
            return parent.get(key.substring("parent.".length()));
        }
        if (parent != null && parent.contains(key)) {
            return parent.get(key);
        }
        return null;
    }
    
    public void put(String key, Object object) {
        map.put(key, object);
    }
    
    public Module getModule() {
        return module;
    }
}
