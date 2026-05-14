package model;


public abstract class Ticket implements Printable {

    protected int ticketId;
    protected double price;
    protected String date;

    public Ticket(int ticketId, double price, String date) {
        this.ticketId = ticketId;
        this.price = price;
        this.date = date;
    }

    public int getTicketId() {
        return ticketId;
    }

    public double getPrice() {
        return calculatePrice();
    }
    
    public String getDate() {
        return date;
    }

    public abstract double calculatePrice();

    @Override
    public void printInfo() {
        System.out.println("Ticket ID: " + ticketId + ", Price: " + price + ", Date: " + date);
    }
}