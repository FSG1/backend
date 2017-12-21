package org.fsg1.fmms.backend.util;

/**
 * Utility class to map numbers to the corresponding architectural layer.
 */
public final class ArchitecturalLayerMapper {
    private ArchitecturalLayerMapper() {

    }

    /**
     * Utility method to map numbers to the corresponding architectural layer.
     *
     * @param activity number of the layer.
     * @return String representation of the layer
     */
    public static String mapInt(final int activity) {
        switch (activity) {
            case 0:
                return "UserInteraction";
            case 1:
                return "BusinessProcess";
            case 2:
                return "Infrastructure";
            case 3:
                return "Software";
            case 4:
                return "HardwareInterfacing";
            default:
                return "UNDEFINED";
        }
    }
}
