package org.fsg1.fmms.backend.util;

public class LifecycleActivityMapper {
    public static String mapInt(final int activity){
        switch(activity){
            case 0: return "Manage";
            case 1: return "Analyse";
            case 2: return "Advise";
            case 3: return "Design";
            case 4: return "Implement";
            default: return "UNDEFINED";
        }
    }
}
