package model;

public class NormalTicket extends Ticket {

    public NormalTicket(int ticketId, double price, String date) {
        super(ticketId, price, date);
    }

    @Override
    public double calculatePrice() {
        return price;
    }
}
