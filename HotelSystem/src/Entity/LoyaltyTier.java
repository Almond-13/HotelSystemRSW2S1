// Author: Lim Jia Zheng
package entity;

public enum LoyaltyTier {
    SILVER(1),
    GOLD(2),
    PLATINUM(3),
    DIAMOND(4),
    ELITE(5);

    private final int priority;

    LoyaltyTier(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
