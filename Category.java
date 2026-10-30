package helpdesk;

public class Category {

    private static int categoryCount = 0;

    private int id;
    private String name;
    private int slaHours;
    private String escalationRules;
    private int breachCount;

    public Category(int id, String name, int slaHours, String escalationRules) {
        this.id = id;
        this.name = name;
        this.slaHours = slaHours;
        this.escalationRules = escalationRules;
        this.breachCount = 0;
        categoryCount++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSlaHours() {
        return slaHours;
    }

    public String getEscalationRules() {
        return escalationRules;
    }

    public int getBreachCount() {
        return breachCount;
    }

    public void setSlaHours(int slaHours) {
        if (slaHours > 0) {
            this.slaHours = slaHours;
        }
    }

    public void setEscalationRules(String escalationRules) {
        this.escalationRules = escalationRules;
    }

    public boolean isBreached(int hoursOpen) {
        return hoursOpen > slaHours;
    }

    // Package-private: the SLA breach counter can only be changed by Ticket.
    void recordBreach() {
        breachCount++;
    }

    public static int getCategoryCount() {
        return categoryCount;
    }

    public void display() {
        System.out.println("Category #" + id + " | " + name + " | SLA: " + slaHours + "h"
                + " | Breaches: " + breachCount + " | Escalation: " + escalationRules);
    }

    @Override
    public String toString() {
        return name;
    }
}
