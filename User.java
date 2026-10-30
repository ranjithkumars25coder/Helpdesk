package helpdesk;

public class User {

    public enum Role { CUSTOMER, AGENT }

    private static int userCount = 0;

    private int id;
    private String name;
    private String email;
    private Role role;

    public User(int id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        userCount++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        } else {
            System.out.println("Invalid email ignored: " + email);
        }
    }

    // Customers cannot work tickets, so their workload limit is zero.
    // Agent overrides this method.
    public int getMaxWorkload() {
        return 0;
    }

    public boolean canHandleTickets() {
        return getMaxWorkload() > 0;
    }

    public static int getUserCount() {
        return userCount;
    }

    public void display() {
        System.out.println("User  #" + id + " | " + name + " | " + email
                + " | Role: " + role + " | Max workload: " + getMaxWorkload());
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + role + ")";
    }
}
