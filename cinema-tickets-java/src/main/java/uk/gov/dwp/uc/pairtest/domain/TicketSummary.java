package uk.gov.dwp.uc.pairtest.domain;

import jakarta.validation.constraints.NotNull;

public class TicketSummary {
    @NotNull
    private final int totalTickets;
    @NotNull
    private final int adultTickets;
    @NotNull
    private final int childTickets;
    @NotNull
    private final int infantTickets;

    private final int totalSeats;
    private final int totalPrice;

    public TicketSummary(int totalTickets, int totalSeats, int totalPrice, int adultTickets, int childTickets,
            int infantTickets) {
        this.totalTickets = totalTickets;
        this.adultTickets = adultTickets;
        this.childTickets = childTickets;
        this.infantTickets = infantTickets;
        this.totalSeats = totalSeats;
        this.totalPrice = totalPrice;
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

    public int getTotalPrice() {
        return totalPrice;
    }
    public int getTotalSeats() {
        return totalSeats;
    }
}
