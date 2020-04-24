package utility;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class AccountDB implements Serializable {
    /** key is username */
    private Map<String, Account> accounts = new HashMap<>();

    public AccountDB () {
        Account melissa = new Account("melissa");
        accounts.put("melissa", melissa);
        melissa.getFiles().add((long)100001);
        melissa.getFiles().add((long)100002);

        Account le = new Account ("le");
        accounts.put ("le", le);
        le.getFiles().add((long) 100001);

        Account muhan = new Account ("muhan");
        accounts.put ("muhan", muhan);
        muhan.getFiles().add((long) 100002);
    }

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
        if (!accounts.containsKey(username)) {
            return null;
        }
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
