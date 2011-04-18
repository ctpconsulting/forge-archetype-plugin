package org.jboss.seam.forge.archetype.model;

import java.util.Map;

class Command {

    private String name;
    private Object[] args;
    
    public Command(String name, Object[] args) {
        this.name = name;
        this.args = args;
    }

    @SuppressWarnings("unchecked")
    public String arg(int i) {
        if (args[i] instanceof Map) { // named args
            Map<String, Object> map = ((Map<String, Object>) args[i]);
            String result = "";
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result = "--" + entry.getKey() + " " + entry.getValue() + " ";
            }
            return result;
        }
        return String.valueOf(args[i]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}
