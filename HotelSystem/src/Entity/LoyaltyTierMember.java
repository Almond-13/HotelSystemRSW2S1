// Author: Lim Jia Zheng
package entity;

public class LoyaltyTierMember {
    private final String memberId;
    private final String guestName;
    private final String phoneNumber;
    private final int historicalRewardPoints;
    private final LoyaltyTier loyaltyTier;

    public LoyaltyTierMember(String memberId, String guestName, String phoneNumber, int historicalRewardPoints) {
        this.memberId = memberId;
        this.guestName = guestName;
        this.phoneNumber = phoneNumber;
        this.historicalRewardPoints = historicalRewardPoints;
        this.loyaltyTier = LoyaltyTier.fromRewardPoints(historicalRewardPoints);
    }

    public String getMemberId() {
        return memberId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getHistoricalRewardPoints() {
        return historicalRewardPoints;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    @Override
    public String toString() {
        return memberId + " | " + guestName + " | " + phoneNumber + " | "
                + loyaltyTier + " | " + historicalRewardPoints + " loyalty points";
    }
}
