package helpdesk;

import java.util.List;

// Ticket opened with HIGH or CRITICAL priority: overrides display() with an escalation banner.
public class EscalatedTicket extends Ticket {

    public EscalatedTicket(User customer, String subject, String description, Priority priority,
                           Category category, List<String> attachments) {
        super(customer, subject, description, priority, category, attachments);
    }

    @Override
    public void display() {
        boolean stillUrgent = getPriority() == Priority.HIGH || getPriority() == Priority.CRITICAL;
        if (stillUrgent && getStatus() != Status.CLOSED) {
            String rule = (getCategory() == null) ? "manager review" : getCategory().getEscalationRules();
            System.out.println("!!! " + getPriority() + " !!! " + summary());
            System.out.println("      SLA used: " + getHoursOpen() + "h | Escalation: " + rule);
        } else {
            super.display();
        }
    }
}
