package utility;

public class LogInMessage extends Message {
    /* Define operation type of the message */
    public enum Action {
        LOGIN,
        CREATE
    }

    private String username;
    private String password;
    private Account account;
    private Action action;

    public LogInMessage () {
        super();
    };

    public LogInMessage (String username) {
        super();
        this.username = username;
    }

    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return this.password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account getAccount() { return this.account; }

    public void setAccount(Account account) { this.account = account; }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
