package exceptions;

public class ConfigurationException extends RuntimeException {
    static final long serialVersionUID = -731385138439L;


    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable parent) {
        super(message, parent);
    }

    public ConfigurationException(Throwable parent) {
        super(parent);
    }
}
