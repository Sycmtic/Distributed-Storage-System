package utility;

public class ClientMessage extends Message {
    private Account user;
    private Action action;

    public void setUser(Account user) {
        this.user = user;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
