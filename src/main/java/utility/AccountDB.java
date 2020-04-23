package utility;

import java.util.Map;
import java.util.HashMap;

public class AccountDB {
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

    /**
     * Get account by username
     * @param username
     * @return account
     */
    public Account getAccount(String username) {
        return accounts.get(username);
    }
}
