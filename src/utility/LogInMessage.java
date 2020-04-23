package utility;

public class LogInMessage extends Message {
    private String username;
    private String password;
    private Account account;

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
}
