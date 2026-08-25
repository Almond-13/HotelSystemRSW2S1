package entity;

import java.time.LocalDate;

public class LoyaltyAccount {
    private String memberId;
    private String guestId;
    private String memberName;
    private String phoneNumber;
    private int totalPoints;
    private int redeemablePoints;
    private int lifetimePoints;
    private TierLevel tierLevel;
    private LocalDate joinDate;
    private LocalDate lastActivityDate;
    private LocalDate pointsExpiryDate;
    private boolean activeStatus;
    private String preferredContactMethod;
    private String notificationMessage;

    public LoyaltyAccount() {
        this.memberId = "";
        this.guestId = "";
        this.memberName = "";
        this.phoneNumber = "";
        this.totalPoints = 0;
        this.redeemablePoints = 0;
        this.lifetimePoints = 0;
        this.tierLevel = TierLevel.BRONZE;
        this.joinDate = LocalDate.now();
        this.lastActivityDate = LocalDate.now();
        this.pointsExpiryDate = LocalDate.now().plusDays(90);
        this.activeStatus = true;
        this.preferredContactMethod = "SMS";
        this.notificationMessage = "";
    }

    public LoyaltyAccount(String memberId, String guestId, String memberName, String phoneNumber) {
        this();
        this.memberId = memberId;
        this.guestId = guestId;
        this.memberName = memberName;
        this.phoneNumber = phoneNumber;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getRedeemablePoints() {
        return redeemablePoints;
    }

    public void setRedeemablePoints(int redeemablePoints) {
        this.redeemablePoints = redeemablePoints;
    }

    public int getLifetimePoints() {
        return lifetimePoints;
    }

    public void setLifetimePoints(int lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }

    public TierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(TierLevel tierLevel) {
        this.tierLevel = tierLevel;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public LocalDate getPointsExpiryDate() {
        return pointsExpiryDate;
    }

    public void setPointsExpiryDate(LocalDate pointsExpiryDate) {
        this.pointsExpiryDate = pointsExpiryDate;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getPreferredContactMethod() {
        return preferredContactMethod;
    }

    public void setPreferredContactMethod(String preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public void addPoints(int pointsToAdd) {
        if (pointsToAdd >= 0) {
            totalPoints += pointsToAdd;
            lifetimePoints += pointsToAdd;
            redeemablePoints += pointsToAdd;
            lastActivityDate = LocalDate.now();
            if (pointsToAdd > 0) {
                pointsExpiryDate = LocalDate.now().plusDays(90);
            }
        }
    }

    public boolean redeemPoints(int pointsToRedeem) {
        if (pointsToRedeem <= 0) {
            return false;
        }

        if (redeemablePoints >= pointsToRedeem) {
            redeemablePoints -= pointsToRedeem;
            totalPoints -= pointsToRedeem;
            lastActivityDate = LocalDate.now();
            if (redeemablePoints <= 0) {
                pointsExpiryDate = LocalDate.now().plusDays(30);
            }
            return true;
        }

        return false;
    }

    public void updateTierFromPoints() {
        if (lifetimePoints >= 5000) {
            tierLevel = TierLevel.PLATINUM;
            notificationMessage = "Congratulations! You reached Platinum tier.";
        } else if (lifetimePoints >= 2500) {
            tierLevel = TierLevel.GOLD;
            notificationMessage = "You are now Gold tier. Enjoy exclusive rewards.";
        } else if (lifetimePoints >= 1000) {
            tierLevel = TierLevel.SILVER;
            notificationMessage = "You are now Silver tier.";
        } else {
            tierLevel = TierLevel.BRONZE;
            notificationMessage = "You are currently Bronze tier. Earn more points to upgrade.";
        }
    }

    public boolean hasExpiringPoints(LocalDate today, int daysThreshold) {
        if (redeemablePoints <= 0 || pointsExpiryDate == null) {
            return false;
        }

        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, pointsExpiryDate);
        return daysRemaining >= 0 && daysRemaining <= daysThreshold;
    }

    public boolean isValid() {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        if (memberName == null || memberName.trim().isEmpty()) {
            return false;
        }
        if (guestId == null || guestId.trim().isEmpty()) {
            return false;
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        if (totalPoints < 0 || redeemablePoints < 0 || lifetimePoints < 0) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{" +
                "memberId='" + memberId + '\'' +
                ", guestId='" + guestId + '\'' +
                ", memberName='" + memberName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", totalPoints=" + totalPoints +
                ", redeemablePoints=" + redeemablePoints +
                ", lifetimePoints=" + lifetimePoints +
                ", tierLevel=" + tierLevel +
                ", joinDate=" + joinDate +
                ", lastActivityDate=" + lastActivityDate +
                ", activeStatus=" + activeStatus +
                ", preferredContactMethod='" + preferredContactMethod + '\'' +
                '}';
    }
}
