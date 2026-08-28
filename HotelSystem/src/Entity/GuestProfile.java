// Author: Lim Jia Zheng
package entity;

public class GuestProfile {
    private final String confirmationNumber;
    private final String guestName;
    private final LoyaltyTier loyaltyTier;
    private final int rewardPoints;

    public GuestProfile(String confirmationNumber, String guestName, LoyaltyTier loyaltyTier, int rewardPoints) {
        this.confirmationNumber = confirmationNumber;
        this.guestName = guestName;
        this.loyaltyTier = loyaltyTier;
        this.rewardPoints = rewardPoints;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    @Override
    public String toString() {
        return confirmationNumber + " | " + guestName + " | " + loyaltyTier + " | "
                + rewardPoints + " loyalty points";
    }
}
