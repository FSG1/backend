package org.fsg1.fmms.backend.exceptions;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Class to map application related exceptions
 */
@XmlRootElement
public class AppException extends Exception implements Serializable {

    /**
     * Contains redundantly the HTTP status of the response sent back to the client in case of error, so that
     * the developer does not have to look into the response headers.
     */
    @XmlElement(name = "status")
    private Integer status;

    /**
     * Message detailing the error.
     */
    @XmlElement(name = "message")
    private String errorMessage;

    /**
     * Detailed error description for developers on how to prevent this error.
     */
    @XmlElement(name = "developerMessage")
    private String developerMessage;

    /**
     * @param status           status code
     * @param errorMessage     error message
     * @param developerMessage specific message for developers
     */
    public AppException(int status, String errorMessage,
                        String developerMessage) {
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
        this.developerMessage = developerMessage;
    }

    public AppException() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}