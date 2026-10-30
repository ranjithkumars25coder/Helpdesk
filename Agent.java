package helpdesk;

public class Agent extends User {

    private static int totalAgents = 0;

    private String specialization;
    private int workloadLimit;
    private int activeTickets;
    private boolean available;

    public Agent(int id, String name, String email, String specialization, int workloadLimit) {
        super(id, name, email, Role.AGENT);
        this.specialization = specialization;
        this.workloadLimit = workloadLimit;
        this.activeTickets = 0;
        this.available = true;
        totalAgents++;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getActiveTickets() {
        return activeTickets;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setWorkloadLimit(int limit) {
        if (limit >= activeTickets) {
            this.workloadLimit = limit;
        } else {
            System.out.println("Limit cannot be below current active tickets (" + activeTickets + ").");
        }
    }

    public static int getTotalAgents() {
        return totalAgents;
    }

    // Overrides User.getMaxWorkload(): agents have a real workload limit.
    @Override
    public int getMaxWorkload() {
        return workloadLimit;
    }

    public boolean hasCapacity() {
        return available && activeTickets < workloadLimit;
    }

    // Package-private: only Ticket/HelpdeskService may change the workload counter.
    void takeTicket() {
        activeTickets++;
    }

    void releaseTicket() {
        if (activeTickets > 0) {
            activeTickets--;
        }
    }

    @Override
    public void display() {
        System.out.println("Agent #" + getId() + " | " + getName() + " | " + getEmail()
                + " | Skill: " + specialization
                + " | Workload: " + activeTickets + "/" + workloadLimit
                + " | " + (available ? "Available" : "Unavailable"));
    }
}
