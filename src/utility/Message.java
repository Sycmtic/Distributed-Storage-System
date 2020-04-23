package utility;

import java.io.Serializable;

public abstract class Message implements Serializable {
    /* Define result type of the message:
       fail: operation is committed with some issues
       success: operation is committed successfully
     */
    public enum Result {
        FAIL,
        SUCCESS
    }

    private Result result = Result.SUCCESS;
    private String errorMessage = "";

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

