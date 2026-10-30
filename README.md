# Helpdesk Ticketing System (Java, OOP)

A console-based Helpdesk application written in Java. It logs support tickets, assigns agents, tracks status and priority, monitors SLA breaches and prints dashboards. Built for **23CSR303 – Object Oriented Programming with Java** (Assignment II).

## Features

- Register customers and agents
- Open tickets with or without a category and attachments
- Assign and reassign agents, with a per-agent workload limit
- Guarded status workflow: `NEW → OPEN → PENDING → RESOLVED → CLOSED`
- Change ticket priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
- SLA tracking per category, with breach counters
- Reports: tickets by priority, by agent, by category, and SLA breaches

## Project Structure

```
helpdesk/
├── User.java              # id, name, email, role (Customer/Agent)
├── Agent.java             # extends User; workload limit, specialization
├── Category.java          # id, name, SLA hours, escalation rules
├── Ticket.java            # ticket data, status/priority, SLA clock
├── EscalatedTicket.java   # extends Ticket; display() for HIGH/CRITICAL
├── HelpdeskService.java   # open/assign/status/priority/reporting
└── HelpdeskAppMain.java   # demo driver (main method)
```

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Inheritance** | `Agent extends User` (overrides `getMaxWorkload()` and `display()`); `EscalatedTicket extends Ticket` |
| **Method overloading** | `HelpdeskService.openTicket()` has three versions: without category, with category, and with category plus attachments |
| **Method overriding** | `display()` in `Ticket` (changes by status) and `EscalatedTicket` (escalation banner for HIGH/CRITICAL) |
| **Polymorphism** | `User` references hold both customers and `Agent` objects; `display()` and `getMaxWorkload()` dispatch at runtime |
| **Encapsulation** | Private fields; status changes go through `Ticket.changeStatus()`, which rejects illegal transitions; the agent workload and SLA breach counters can only be changed inside the package |

## Status Workflow

```
NEW → OPEN → PENDING → RESOLVED → CLOSED
          ↖__________↙
        (PENDING can return to OPEN)
```

A ticket must have an assigned agent before it can move to `OPEN`. `CLOSED` is final.

## Requirements

- JDK 11 or later (developed and tested with JDK 21)

## How to Compile and Run

From the folder that contains the `helpdesk` directory:

```bash
javac helpdesk/*.java
java helpdesk.HelpdeskAppMain
```

Or, if you keep sources under `src/` as in this repository:

```bash
mkdir out
javac -d out src/helpdesk/*.java
java -cp out helpdesk.HelpdeskAppMain
```

## What the Demo Does

`HelpdeskAppMain` walks through a full scenario:

1. Creates three categories (Network, Billing, Software), each with an SLA and escalation rule
2. Registers two customers and three agents
3. Opens four tickets using the overloaded `openTicket()` methods, and shows an agent being refused when trying to raise a ticket
4. Assigns and reassigns agents, including a refusal when an agent reaches their workload limit
5. Moves tickets through the status workflow and shows illegal transitions being blocked
6. Advances the clock to trigger SLA breaches
7. Prints the dashboards: tickets by priority, by agent, by category, and SLA breaches

### Sample Output (excerpt)

```
===== STATUS WORKFLOW =====
Illegal transition for ticket #1001: NEW -> RESOLVED
Ticket #1001: NEW -> OPEN
Ticket #1001: OPEN -> PENDING
Ticket #1001: PENDING -> RESOLVED
Ticket #1001: RESOLVED -> CLOSED
Illegal transition for ticket #1001: CLOSED -> OPEN

===== DASHBOARD: SLA BREACHES =====
BREACH #1002 Invoice amount wrong | open 26h | SLA 24h | Billing | Escalate to Finance Manager after 24h
BREACH #1004 App crashes on login | open 26h | SLA 8h | Software | Escalate to Dev Lead after 8h
Total breaches: 2
```


Course: 23CSR303 Object Oriented Programming with Java
Department of Computer Science and Engineering, Karpagam College of Engineering
