package helpdesk;

import java.util.Arrays;

public class HelpdeskAppMain {

    public static void main(String[] args) {
        HelpdeskService service = new HelpdeskService("Campus IT Helpdesk");
        System.out.println("===== " + service.getServiceName().toUpperCase() + " =====\n");

        // 1. Categories
        Category network = new Category(1, "Network", 4, "Escalate to Network Lead after 4h");
        Category billing = new Category(2, "Billing", 24, "Escalate to Finance Manager after 24h");
        Category software = new Category(3, "Software", 8, "Escalate to Dev Lead after 8h");
        service.addCategory(network);
        service.addCategory(billing);
        service.addCategory(software);

        // 2. Register users (Customer and Agent through User references)
        User c1 = new User(1, "Anita", "anita@mail.com", User.Role.CUSTOMER);
        User c2 = new User(2, "Bala", "bala@mail.com", User.Role.CUSTOMER);
        User a1 = new Agent(11, "Ravi", "ravi@help.com", "Network", 2);
        User a2 = new Agent(12, "Meena", "meena@help.com", "Billing", 1);
        User a3 = new Agent(13, "Suresh", "suresh@help.com", "Software", 3);
        service.registerUser(c1);
        service.registerUser(c2);
        service.registerUser(a1);
        service.registerUser(a2);
        service.registerUser(a3);
        service.listUsers();

        // 3. Create tickets (overloaded openTicket)
        System.out.println("\n===== CREATING TICKETS =====");
        Ticket t1 = service.openTicket(c1, "VPN not connecting", "Cannot reach office VPN",
                Ticket.Priority.CRITICAL, network, Arrays.asList("vpn_log.txt", "screenshot.png"));
        Ticket t2 = service.openTicket(c2, "Invoice amount wrong", "Charged twice for March",
                Ticket.Priority.MEDIUM, billing);
        Ticket t3 = service.openTicket(c1, "Printer offline", "Third floor printer not responding",
                Ticket.Priority.LOW);
        Ticket t4 = service.openTicket(c2, "App crashes on login", "Crash after the latest update",
                Ticket.Priority.HIGH, software);
        service.openTicket(a1, "Agent trying to raise ticket", "Should be rejected", Ticket.Priority.LOW);

        // 4. Assign / reassign
        System.out.println("\n===== ASSIGNING AGENTS =====");
        service.assignAgent(t1.getTicketId(), 11);
        service.assignAgent(t2.getTicketId(), 12);
        service.assignAgent(t3.getTicketId(), 12);   // Meena limit is 1 -> refused
        service.assignAgent(t3.getTicketId(), 11);
        service.assignAgent(t4.getTicketId(), 13);
        service.reassignAgent(t3.getTicketId(), 13); // reassign printer ticket to Suresh

        service.dashboardByAgent();

        // 5. Status workflow NEW -> OPEN -> PENDING -> RESOLVED -> CLOSED
        System.out.println("\n===== STATUS WORKFLOW =====");
        service.changeStatus(t1.getTicketId(), Ticket.Status.RESOLVED); // illegal from NEW
        service.changeStatus(t1.getTicketId(), Ticket.Status.OPEN);
        service.changeStatus(t1.getTicketId(), Ticket.Status.PENDING);
        service.changeStatus(t1.getTicketId(), Ticket.Status.RESOLVED);
        service.changeStatus(t1.getTicketId(), Ticket.Status.CLOSED);
        service.changeStatus(t1.getTicketId(), Ticket.Status.OPEN);     // illegal from CLOSED

        service.changeStatus(t2.getTicketId(), Ticket.Status.OPEN);
        service.changeStatus(t4.getTicketId(), Ticket.Status.OPEN);
        service.changeStatus(t4.getTicketId(), Ticket.Status.PENDING);
        service.changePriority(t3.getTicketId(), Ticket.Priority.MEDIUM);
        service.changePriority(t1.getTicketId(), Ticket.Priority.LOW);  // closed -> refused

        // 6. SLA clock
        service.advanceTime(6);
        service.advanceTime(20);

        // 7. Reports
        service.listByPriority();
        service.dashboardByAgent();
        service.dashboardByCategory();
        service.dashboardSlaBreaches();

        System.out.println("\nTotal tickets created: " + HelpdeskService.getTicketsCreated());
        System.out.println("Total users: " + User.getUserCount() + " (agents: " + Agent.getTotalAgents() + ")");
    }
}
