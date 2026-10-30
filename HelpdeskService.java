package helpdesk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HelpdeskService {

    private static int ticketsCreated = 0;

    private List<User> users;
    private List<Ticket> tickets;
    private List<Category> categories;
    private String serviceName;

    public HelpdeskService(String serviceName) {
        this.serviceName = serviceName;
        this.users = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public String getServiceName() {
        return serviceName;
    }

    public static int getTicketsCreated() {
        return ticketsCreated;
    }

    // ---------- registration ----------

    // Polymorphism: accepts both User and Agent through a User reference.
    public void registerUser(User user) {
        users.add(user);
        System.out.println("Registered: " + user.getName() + " as " + user.getRole());
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public User findUser(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public Ticket findTicket(int ticketId) {
        for (Ticket t : tickets) {
            if (t.getTicketId() == ticketId) {
                return t;
            }
        }
        return null;
    }

    // ---------- openTicket (overloaded) ----------

    public Ticket openTicket(User customer, String subject, String description, Ticket.Priority priority) {
        return openTicket(customer, subject, description, priority, null, null);
    }

    public Ticket openTicket(User customer, String subject, String description, Ticket.Priority priority,
                             Category category) {
        return openTicket(customer, subject, description, priority, category, null);
    }

    public Ticket openTicket(User customer, String subject, String description, Ticket.Priority priority,
                             Category category, List<String> attachments) {
        if (customer == null || customer.getRole() != User.Role.CUSTOMER) {
            System.out.println("Only a registered customer can raise a ticket.");
            return null;
        }
        Ticket ticket;
        if (priority == Ticket.Priority.HIGH || priority == Ticket.Priority.CRITICAL) {
            ticket = new EscalatedTicket(customer, subject, description, priority, category, attachments);
        } else {
            ticket = new Ticket(customer, subject, description, priority, category, attachments);
        }
        tickets.add(ticket);
        ticketsCreated++;
        System.out.println("Ticket created: #" + ticket.getTicketId() + " \"" + subject + "\" by "
                + customer.getName());
        return ticket;
    }

    // ---------- assignment and updates ----------

    public boolean assignAgent(int ticketId, int agentId) {
        Ticket ticket = findTicket(ticketId);
        User user = findUser(agentId);
        if (ticket == null || !(user instanceof Agent)) {
            System.out.println("Assignment failed: invalid ticket #" + ticketId + " or agent id " + agentId);
            return false;
        }
        boolean ok = ticket.assignAgent((Agent) user);
        if (ok) {
            System.out.println("Ticket #" + ticketId + " assigned to " + user.getName());
        }
        return ok;
    }

    public boolean reassignAgent(int ticketId, int newAgentId) {
        Ticket ticket = findTicket(ticketId);
        User user = findUser(newAgentId);
        if (ticket == null || !(user instanceof Agent)) {
            System.out.println("Reassignment failed.");
            return false;
        }
        String previous = (ticket.getAssignee() == null) ? "nobody" : ticket.getAssignee().getName();
        boolean ok = ticket.assignAgent((Agent) user);
        if (ok) {
            System.out.println("Ticket #" + ticketId + " reassigned from " + previous + " to " + user.getName());
        }
        return ok;
    }

    public boolean changeStatus(int ticketId, Ticket.Status status) {
        Ticket ticket = findTicket(ticketId);
        if (ticket == null) {
            System.out.println("Ticket #" + ticketId + " not found.");
            return false;
        }
        Ticket.Status old = ticket.getStatus();
        boolean ok = ticket.changeStatus(status);
        if (ok) {
            System.out.println("Ticket #" + ticketId + ": " + old + " -> " + status);
        }
        return ok;
    }

    public boolean changePriority(int ticketId, Ticket.Priority priority) {
        Ticket ticket = findTicket(ticketId);
        if (ticket == null) {
            return false;
        }
        Ticket.Priority old = ticket.getPriority();
        boolean ok = ticket.setPriority(priority);
        if (ok) {
            System.out.println("Ticket #" + ticketId + " priority: " + old + " -> " + priority);
        }
        return ok;
    }

    // Simulates the passing of time so SLA clocks advance.
    public void advanceTime(int hours) {
        for (Ticket t : tickets) {
            t.addHours(hours);
        }
        System.out.println("\n[Clock advanced by " + hours + " hour(s)]");
    }

    // ---------- reporting ----------

    public void listUsers() {
        System.out.println("\n===== USERS (via User references) =====");
        for (User u : users) {
            u.display();
        }
    }

    public void listByPriority() {
        System.out.println("\n===== TICKETS BY PRIORITY (highest first) =====");
        List<Ticket> sorted = new ArrayList<>(tickets);
        sorted.sort(Comparator.comparingInt((Ticket t) -> t.getPriority().getWeight()).reversed());
        for (Ticket t : sorted) {
            t.display();
        }
    }

    public void dashboardByAgent() {
        System.out.println("\n===== DASHBOARD: TICKETS BY AGENT =====");
        for (User u : users) {
            if (u instanceof Agent) {
                Agent a = (Agent) u;
                System.out.println(a.getName() + " (" + a.getSpecialization() + ") - active "
                        + a.getActiveTickets() + "/" + a.getMaxWorkload());
                boolean any = false;
                for (Ticket t : tickets) {
                    if (t.getAssignee() == a) {
                        System.out.println("    #" + t.getTicketId() + " " + t.getSubject()
                                + " [" + t.getStatus() + "]");
                        any = true;
                    }
                }
                if (!any) {
                    System.out.println("    (no tickets)");
                }
            }
        }
        System.out.println("Unassigned:");
        boolean none = true;
        for (Ticket t : tickets) {
            if (t.getAssignee() == null) {
                System.out.println("    #" + t.getTicketId() + " " + t.getSubject());
                none = false;
            }
        }
        if (none) {
            System.out.println("    (none)");
        }
    }

    public void dashboardByCategory() {
        System.out.println("\n===== DASHBOARD: TICKETS BY CATEGORY =====");
        for (Category c : categories) {
            int count = 0;
            for (Ticket t : tickets) {
                if (t.getCategory() == c) {
                    count++;
                }
            }
            System.out.println(c.getName() + " (SLA " + c.getSlaHours() + "h): " + count + " ticket(s)");
            for (Ticket t : tickets) {
                if (t.getCategory() == c) {
                    System.out.println("    #" + t.getTicketId() + " " + t.getSubject()
                            + " [" + t.getStatus() + "]");
                }
            }
        }
        int uncategorised = 0;
        for (Ticket t : tickets) {
            if (t.getCategory() == null) {
                uncategorised++;
            }
        }
        System.out.println("Uncategorised: " + uncategorised + " ticket(s)");
    }

    public void dashboardSlaBreaches() {
        System.out.println("\n===== DASHBOARD: SLA BREACHES =====");
        int total = 0;
        for (Ticket t : tickets) {
            if (t.isSlaBreached()) {
                total++;
                System.out.println("BREACH #" + t.getTicketId() + " " + t.getSubject() + " | open "
                        + t.getHoursOpen() + "h | SLA " + t.getCategory().getSlaHours() + "h | "
                        + t.getCategory().getName() + " | " + t.getCategory().getEscalationRules());
            }
        }
        if (total == 0) {
            System.out.println("No SLA breaches.");
        }
        System.out.println("Total breaches: " + total);
        for (Category c : categories) {
            System.out.println("    " + c.getName() + ": " + c.getBreachCount());
        }
    }
}
