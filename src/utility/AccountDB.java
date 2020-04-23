package utility;

import java.util.Map;
import java.util.HashMap;

public class AccountDB {
    /** key is username */
    private Map<String, Account> accounts;

    public AccountDB() { accounts = new HashMap<>(); }

    public AccountDB(AccountDB accountDB) {
        accounts = new HashMap<>();
        accounts.putAll(accountDB.getAccounts());
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    /**
     * Get account by username
     * @param username
     * @return account
     */
    public Account getAccount(String username) {
        return accounts.get(username);
    }

    /**
     * Create a new account
     * @param username
     * @return
     */
    public Account createAccount(String username) {
        Account account = new Account(username);
        accounts.put(username, account);
        return account;
    }
}
