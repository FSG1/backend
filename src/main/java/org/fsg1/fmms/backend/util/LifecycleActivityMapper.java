package org.fsg1.fmms.backend.util;

/**
 * Utility method to map numbers to the corresponding lifecycle activity.
 */
public final class LifecycleActivityMapper {
    private LifecycleActivityMapper() {

    }

    /**
     * Utility method to map numbers to the corresponding lifecycle activity.
     *
     * @param activity number of the activity.
     * @return String representation of the activity
     */
    public static String mapInt(final int activity) {
        switch (activity) {
            case 0:
                return "Manage";
            case 1:
                return "Analyse";
            case 2:
                return "Advise";
            case 3:
                return "Design";
            case 4:
                return "Implement";
            default:
                return "UNDEFINED";
        }
    }
}
