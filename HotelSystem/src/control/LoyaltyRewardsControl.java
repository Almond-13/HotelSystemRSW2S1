package control;

import java.time.LocalDate;
import adt.ArrayList;
import entity.LoyaltyAccount;
import entity.TierLevel;

public class LoyaltyRewardsControl {
    private ArrayList<LoyaltyAccount> accounts;
    private int nextMemberNumber;
    private String lastError;

    public LoyaltyRewardsControl() {
        accounts = new ArrayList<>();
        nextMemberNumber = 1;
        lastError = "";
        seedSampleData();
    }

    public String getLastError() {
        return lastError;
    }

    private void setError(String message) {
        this.lastError = message;
    }

    private void clearError() {
        this.lastError = "";
    }

    public void seedSampleData() {
        LoyaltyAccount a1 = new LoyaltyAccount(generateMemberId(), "G001", "Alice Tan", "0123456789");
        a1.addPoints(1200);
        a1.updateTierFromPoints();
        addAccount(a1);

        LoyaltyAccount a2 = new LoyaltyAccount(generateMemberId(), "G002", "Ben Lim", "0134567891");
        a2.addPoints(2400);
        a2.updateTierFromPoints();
        addAccount(a2);

        LoyaltyAccount a3 = new LoyaltyAccount(generateMemberId(), "G003", "Chloe Ng", "0145678912");
        a3.addPoints(3500);
        a3.updateTierFromPoints();
        addAccount(a3);

        LoyaltyAccount a4 = new LoyaltyAccount(generateMemberId(), "G004", "David Ong", "0156789123");
        a4.addPoints(5200);
        a4.updateTierFromPoints();
        addAccount(a4);
    }

    public String generateMemberId() {
        String memberId = "LM" + String.format("%03d", nextMemberNumber);
        nextMemberNumber++;
        return memberId;
    }

    public boolean addAccount(LoyaltyAccount account) {
        if (account == null) {
            setError("Account object cannot be null.");
            return false;
        }

        if (!account.isValid()) {
            setError("Account data is incomplete or invalid. All fields are required.");
            return false;
        }

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getMemberId().equals(account.getMemberId())) {
                setError("Member ID '" + account.getMemberId() + "' already exists.");
                return false;
            }
        }

        accounts.add(account);
        account.updateTierFromPoints();
        clearError();
        return true;
    }

    public LoyaltyAccount findAccountByMemberId(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            setError("Member ID cannot be empty.");
            return null;
        }

        for (int i = 0; i < accounts.size(); i++) {
            LoyaltyAccount account = accounts.get(i);
            if (account.getMemberId().equalsIgnoreCase(memberId.trim())) {
                clearError();
                return account;
            }
        }

        setError("Member ID '" + memberId + "' not found.");
        return null;
    }

    public boolean addPoints(String memberId, int pointsToAdd) {
        if (memberId == null || memberId.trim().isEmpty()) {
            setError("Member ID cannot be empty.");
            return false;
        }

        if (pointsToAdd < 0) {
            setError("Points cannot be negative. Minimum value is 0.");
            return false;
        }

        if (pointsToAdd == 0) {
            setError("Please add at least 1 point.");
            return false;
        }

        LoyaltyAccount account = findAccountByMemberId(memberId);
        if (account == null) {
            setError("Member ID '" + memberId + "' not found.");
            return false;
        }

        account.addPoints(pointsToAdd);
        account.updateTierFromPoints();
        clearError();
        return true;
    }

    public boolean redeemPoints(String memberId, int pointsToRedeem) {
        if (memberId == null || memberId.trim().isEmpty()) {
            setError("Member ID cannot be empty.");
            return false;
        }

        if (pointsToRedeem < 0) {
            setError("Points cannot be negative. Minimum value is 0.");
            return false;
        }

        if (pointsToRedeem == 0) {
            setError("Please redeem at least 1 point.");
            return false;
        }

        LoyaltyAccount account = findAccountByMemberId(memberId);
        if (account == null) {
            setError("Member ID '" + memberId + "' not found.");
            return false;
        }

        if (account.getRedeemablePoints() < pointsToRedeem) {
            setError("Insufficient redeemable points. You have " + account.getRedeemablePoints() + " points.");
            return false;
        }

        boolean success = account.redeemPoints(pointsToRedeem);
        if (success) {
            account.updateTierFromPoints();
            clearError();
            return true;
        } else {
            setError("Redemption failed. Please try again.");
            return false;
        }
    }

    public boolean updateMemberTier(String memberId) {
        LoyaltyAccount account = findAccountByMemberId(memberId);
        if (account == null) {
            return false;
        }

        account.updateTierFromPoints();
        return true;
    }

    public ArrayList<LoyaltyAccount> searchByMemberName(String keyword) {
        ArrayList<LoyaltyAccount> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String lowerKeyword = keyword.trim().toLowerCase();
        for (int i = 0; i < accounts.size(); i++) {
            LoyaltyAccount account = accounts.get(i);
            if (account.getMemberName().toLowerCase().contains(lowerKeyword)) {
                result.add(account);
            }
        }

        return result;
    }

    public ArrayList<LoyaltyAccount> filterByTier(TierLevel tierLevel) {
        ArrayList<LoyaltyAccount> result = new ArrayList<>();

        if (tierLevel == null) {
            return result;
        }

        for (int i = 0; i < accounts.size(); i++) {
            LoyaltyAccount account = accounts.get(i);
            if (account.getTierLevel() == tierLevel) {
                result.add(account);
            }
        }

        return result;
    }

    public ArrayList<LoyaltyAccount> filterByPointsRange(int minimum, int maximum) {
        ArrayList<LoyaltyAccount> result = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            LoyaltyAccount account = accounts.get(i);
            int points = account.getTotalPoints();
            if (points >= minimum && points <= maximum) {
                result.add(account);
            }
        }

        return result;
    }

    public ArrayList<LoyaltyAccount> getExpiringPointsReport(LocalDate today, int daysThreshold) {
        ArrayList<LoyaltyAccount> result = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            LoyaltyAccount account = accounts.get(i);
            if (account.hasExpiringPoints(today, daysThreshold)) {
                result.add(account);
            }
        }

        return result;
    }

    public ArrayList<LoyaltyAccount> sortByTotalPointsDescending(ArrayList<LoyaltyAccount> source) {
        ArrayList<LoyaltyAccount> sorted = cloneAccountList(source);

        for (int i = 1; i < sorted.size(); i++) {
            LoyaltyAccount current = sorted.get(i);
            int j = i - 1;

            while (j >= 0 && sorted.get(j).getTotalPoints() < current.getTotalPoints()) {
                sorted.replace(j + 1, sorted.get(j));
                j--;
            }

            sorted.replace(j + 1, current);
        }

        return sorted;
    }

    public ArrayList<LoyaltyAccount> sortByNameAscending(ArrayList<LoyaltyAccount> source) {
        ArrayList<LoyaltyAccount> sorted = cloneAccountList(source);

        for (int i = 1; i < sorted.size(); i++) {
            LoyaltyAccount current = sorted.get(i);
            int j = i - 1;

            while (j >= 0 && sorted.get(j).getMemberName().compareToIgnoreCase(current.getMemberName()) > 0) {
                sorted.replace(j + 1, sorted.get(j));
                j--;
            }

            sorted.replace(j + 1, current);
        }

        return sorted;
    }

    private ArrayList<LoyaltyAccount> cloneAccountList(ArrayList<LoyaltyAccount> source) {
        ArrayList<LoyaltyAccount> clone = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            clone.add(source.get(i));
        }
        return clone;
    }

    public ArrayList<LoyaltyAccount> buildTierReport(TierLevel tierLevel, String searchText) {
        ArrayList<LoyaltyAccount> filtered = filterByTier(tierLevel);
        ArrayList<LoyaltyAccount> searched = new ArrayList<>();

        if (searchText == null || searchText.trim().isEmpty()) {
            searched = filtered;
        } else {
            String keyword = searchText.trim().toLowerCase();
            for (int i = 0; i < filtered.size(); i++) {
                LoyaltyAccount account = filtered.get(i);
                if (account.getMemberName().toLowerCase().contains(keyword)
                        || account.getMemberId().toLowerCase().contains(keyword)) {
                    searched.add(account);
                }
            }
        }

        return sortByTotalPointsDescending(searched);
    }

    public ArrayList<LoyaltyAccount> buildExpiringPointsReport(LocalDate today, int daysThreshold,
            TierLevel tierLevel) {
        ArrayList<LoyaltyAccount> candidates = getExpiringPointsReport(today, daysThreshold);
        ArrayList<LoyaltyAccount> filtered = new ArrayList<>();

        if (tierLevel == null) {
            filtered = candidates;
        } else {
            for (int i = 0; i < candidates.size(); i++) {
                LoyaltyAccount account = candidates.get(i);
                if (account.getTierLevel() == tierLevel) {
                    filtered.add(account);
                }
            }
        }

        ArrayList<LoyaltyAccount> withNotifications = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            LoyaltyAccount account = filtered.get(i);
            String message = "Expiry warning: " + account.getRedeemablePoints() + " points due by "
                    + account.getPointsExpiryDate();
            account.setNotificationMessage(message);
            withNotifications.add(account);
        }

        return sortByNameAscending(withNotifications);
    }

    public ArrayList<LoyaltyAccount> getAllAccounts() {
        return accounts;
    }

    public int getAccountCount() {
        return accounts.size();
    }
}
