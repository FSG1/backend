package org.fsg1.fmms.backend.util;

public class ArchitecturalLayerMapper {
    public static String mapInt(final int activity){
        switch(activity){
            case 0: return "UserInteraction";
            case 1: return "BusinessProcess";
            case 2: return "Infrastructure";
            case 3: return "Software";
            case 4: return "HardwareInterfacing";
            default: return "UNDEFINED";
        }
    }
}
