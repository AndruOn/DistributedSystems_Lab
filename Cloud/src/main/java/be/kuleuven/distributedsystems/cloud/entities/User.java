package be.kuleuven.distributedsystems.cloud.entities;

import java.util.List;

public class User {

    private String email;
    private String role;
    //private List<Booking> bookings;

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isManager() {
        return this.role != null && this.role.equals("manager");
    }

    //public List<Booking> getBookings(){return this.bookings;}
}
