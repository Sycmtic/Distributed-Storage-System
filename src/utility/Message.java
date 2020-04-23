package utility;

import java.io.Serializable;

public abstract class Message implements Serializable {
    /* Define operation type of the message */
    public enum Operation {
        LOGIN,
        CREATE,
        MODIFY,
        ACCOUNT,
        SHARE
    }

    private Operation op;

    public Operation getOp() {
        return this.op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

}
