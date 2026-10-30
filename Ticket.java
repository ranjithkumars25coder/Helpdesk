package helpdesk;

import java.util.ArrayList;
import java.util.List;

public class Ticket {

    public enum Priority {
        LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

        private final int weight;

        Priority(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }
    }

    public enum Status {
        NEW, OPEN, PENDING, RESOLVED, CLOSED;

        // Allowed workflow: NEW -> OPEN -> PENDING -> RESOLVED -> CLOSED
        // (PENDING may also go back to OPEN when the customer replies)
        public boolean canMoveTo(Status next) {
            switch (this) {
                case NEW:
                    return next == OPEN;
                case OPEN:
                    return next == PENDING || next == RESOLVED;
                case PENDING:
                    return next == OPEN || next == RESOLVED;
                case RESOLVED:
                    return next == CLOSED;
                default:
                    return false;
            }
        }
    }

    private static int ticketCounter = 1000;

    private final int ticketId;
    private String subject;
    private String description;
    private Priority priority;
    private Status status;
    private Agent assignee;
    private Category category;
    private User customer;
    private List<String> attachments;
    private int hoursOpen;
    private boolean slaBreached;

    public Ticket(User customer, String subject, String description, Priority priority,
                  Category category, List<String> attachments) {
        this.ticketId = ++ticketCounter;
        this.customer = customer;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.attachments = (attachments == null) ? new ArrayList<>() : new ArrayList<>(attachments);
        this.status = Status.NEW;
        this.assignee = null;
        this.hoursOpen = 0;
        this.slaBreached = false;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public Agent getAssignee() {
        return assignee;
    }

    public Category getCategory() {
        return category;
    }

    public User getCustomer() {
        return customer;
    }

    public List<String> getAttachments() {
        return new ArrayList<>(attachments);
    }

    public int getHoursOpen() {
        return hoursOpen;
    }

    public boolean isSlaBreached() {
        return slaBreached;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean setPriority(Priority priority) {
        if (status == Status.CLOSED) {
            System.out.println("Ticket #" + ticketId + " is closed; priority cannot change.");
            return false;
        }
        this.priority = priority;
        return true;
    }

    public void addAttachment(String file) {
        attachments.add(file);
    }

    // Assign or reassign an agent; keeps the agent workload counters consistent.
    public boolean assignAgent(Agent agent) {
        if (agent == null) {
            return false;
        }
        if (status == Status.RESOLVED || status == Status.CLOSED) {
            System.out.println("Ticket #" + ticketId + " is " + status + "; it cannot be assigned.");
            return false;
        }
        if (assignee == agent) {
            System.out.println("Ticket #" + ticketId + " is already assigned to " + agent.getName());
            return false;
        }
        if (!agent.hasCapacity()) {
            System.out.println("Agent " + agent.getName() + " cannot take more tickets (workload "
                    + agent.getActiveTickets() + "/" + agent.getMaxWorkload() + ").");
            return false;
        }
        if (assignee != null) {
            assignee.releaseTicket();
        }
        assignee = agent;
        agent.takeTicket();
        return true;
    }

    // Guarded status transition.
    public boolean changeStatus(Status next) {
        if (!status.canMoveTo(next)) {
            System.out.println("Illegal transition for ticket #" + ticketId + ": " + status + " -> " + next);
            return false;
        }
        if (next == Status.OPEN && assignee == null) {
            System.out.println("Ticket #" + ticketId + " needs an assigned agent before it can be OPEN.");
            return false;
        }
        if (next == Status.RESOLVED && assignee != null) {
            assignee.releaseTicket();
        }
        status = next;
        return true;
    }

    // SLA clock: only unresolved tickets accumulate time.
    public void addHours(int hours) {
        if (hours <= 0 || status == Status.RESOLVED || status == Status.CLOSED) {
            return;
        }
        hoursOpen += hours;
        if (!slaBreached && category != null && category.isBreached(hoursOpen)) {
            slaBreached = true;
            category.recordBreach();
        }
    }

    protected String summary() {
        return "#" + ticketId + " | " + subject + " | " + priority + " | " + status
                + " | " + (category == null ? "Uncategorised" : category.getName())
                + " | Agent: " + (assignee == null ? "-" : assignee.getName());
    }

    // Status-specific display.
    public void display() {
        String note;
        switch (status) {
            case NEW:
                note = (assignee == null) ? "awaiting assignment" : "assigned, not yet opened";
                break;
            case OPEN:
                note = "being worked on";
                break;
            case PENDING:
                note = "waiting for customer reply";
                break;
            case RESOLVED:
                note = "awaiting closure";
                break;
            default:
                note = "archived";
        }
        System.out.println(summary() + " -> " + note);
    }
}
