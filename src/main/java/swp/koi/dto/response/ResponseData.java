package swp.koi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
    private int status;                 //Response code
    private String message;             //Response message
    private T data;                     //Generic Type
    private ResponseCode responseCode;  // Custom response code

    /**
     * Constructor to create a ResponseData with just the status code and message.
     *
     * @param status the HTTP status code
     * @param message the response message
     */
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }
    /**
     * Constructor to create a ResponseData with status code, message, and data.
     *
     * @param status the HTTP status code
     * @param message the response message
     * @param data the response data
     */
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    /**
     * Constructor to create a ResponseData from a ResponseCode and data.
     *
     * @param responseCode custom response code containing the code and message
     * @param data the response data
     */
    public ResponseData(ResponseCode responseCode, T data) {
        this.status = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }
    /**
     * Constructor to create a ResponseData object using a ResponseCode.
     *
     * @param responseCode - the ResponseCode enum containing the status and message.
     */
    public ResponseData(ResponseCode responseCode) {
        this.status = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    // Getters and setters
    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {

        this.status = status;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public T getData() {

        return data;
    }

    public void setData(T data) {

        this.data = data;
    }
}
