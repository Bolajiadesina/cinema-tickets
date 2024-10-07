package uk.gov.dwp.uc.pairtest.domain;


// TicketSummary: helper class to encapsulate the summary of tickets and payments.
public class TicketSummary {
    private final int totalTickets;
    private final int adultTickets;
    private final int childTickets;
    private final int infantTickets;
    private final int totalAmountToPay;

    public TicketSummary(int totalTickets, int adultTickets, int childTickets, int infantTickets, int totalAmountToPay) {
        this.totalTickets = totalTickets;
        this.adultTickets = adultTickets;
        this.childTickets = childTickets;
        this.infantTickets = infantTickets;
        this.totalAmountToPay = totalAmountToPay;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getAdultTickets() {
        return adultTickets;
    }

    public int getChildTickets() {
        return childTickets;
    }

    public int getInfantTickets() {
        return infantTickets;
    }

    public int getTotalAmountToPay() {
        return totalAmountToPay;
    }
}






























