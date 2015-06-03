package dov.projects.restfulskeleton.controllers.errorhandling;

public class ErrorResponse {
    private int status;
    private int code;
    private String message;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;

        ErrorResponse that = (ErrorResponse) o;

        if (status != that.status) return false;
        if (code != that.code) return false;
        return !(message != null ? !message.equals(that.message) : that.message != null);

    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + code;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
