package utility;

import java.util.Map;
import java.util.HashMap;

public class AccountDB {
    /** key is username */
    private Map<String, Account> accounts = new HashMap<>();

    /**
     * Get account by username
     * @param username
     * @return account
     */
    public Account getAccount(String username) {
        return accounts.get(username);
    }
}
