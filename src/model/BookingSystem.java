package model;

public class BookingSystem {

    private Passenger passenger;
    private Ticket ticket;

    public BookingSystem(Passenger passenger, Ticket ticket) {
        this.passenger = passenger;
        this.ticket = ticket;
    }

    public void printBooking() {
        System.out.println("\n--- Booking Details ---");
        passenger.printInfo();
        ticket.printInfo();
        System.out.println("Final Price: " + ticket.calculatePrice());
    }
}
