package model;

import java.util.ArrayList;

public class Train {

    private String trainName;
    private ArrayList<Ticket> tickets = new ArrayList<>();

    public Train(String trainName) {
        this.trainName = trainName;
    }

    public void addTicket(Ticket t) {
        tickets.add(t);
    }

    // linear search by ticket ID
    public Ticket searchTicketById(int id) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId() == id) {
                return tickets.get(i);
            }
        }
        return null;
    }

    // bubble sort by calculated price
    public void sortTicketsByPrice() {
        int n = tickets.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (tickets.get(j).calculatePrice() > tickets.get(j + 1).calculatePrice()) {
                    Ticket tmp = tickets.get(j);
                    tickets.set(j, tickets.get(j + 1));
                    tickets.set(j + 1, tmp);
                }
            }
        }
    }

    public void printTickets() {
        for (int i = 0; i < tickets.size(); i++) {
            tickets.get(i).printInfo();
        }
    }

    public String getAllTicketsAsString() {
        sortTicketsByPrice();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            sb.append("ID: ").append(t.getTicketId())
              .append("  Price: ").append(t.calculatePrice())
              .append("\n");
        }
        return sb.toString();
    }

    public String getTrainName() {
        return trainName;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }
}