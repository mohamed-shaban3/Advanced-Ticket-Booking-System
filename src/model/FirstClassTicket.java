package model;

public class FirstClassTicket extends Ticket {

    public FirstClassTicket(int ticketId, double price, String date) {
        super(ticketId, price, date);
    }

    @Override
    public double calculatePrice() {
        return price * 1.5;
    }
}
