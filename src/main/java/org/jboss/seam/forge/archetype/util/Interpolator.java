package org.jboss.seam.forge.archetype.util;

import java.util.StringTokenizer;

import org.jboss.seam.forge.archetype.model.ArchetypeContext;

public abstract class Interpolator {

    public static String interpolate(String string, ArchetypeContext context) {
        if (!string.contains("#{"))
            return string;
        StringTokenizer tokens = new StringTokenizer(string, "#{}", true);
        StringBuilder builder = new StringBuilder(string.length());
        while (tokens.hasMoreTokens()) {
            String tok = tokens.nextToken();
            if ("#".equals(tok) && tokens.hasMoreTokens()) {
                String nextTok = tokens.nextToken();
                while (nextTok.equals("#") && tokens.hasMoreTokens()) {
                    builder.append(tok);
                    nextTok = tokens.nextToken();
                }
                if ("{".equals(nextTok)) {
                    Object value = context.get(tokens.nextToken());
                    if (value != null)
                        builder.append(value);
                    tokens.nextToken();
                    // the trailing "}"
                } else if (nextTok.equals("#")) {
                    // could be trailing #
                    builder.append("#");
                } else {
                    builder.append("#").append(nextTok);
                }
            } else {
                builder.append(tok);
            }
        }
        return builder.toString();
    }

}
