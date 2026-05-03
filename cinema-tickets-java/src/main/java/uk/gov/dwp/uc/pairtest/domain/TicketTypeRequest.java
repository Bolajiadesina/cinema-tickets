package uk.gov.dwp.uc.pairtest.domain;

import jakarta.validation.constraints.NotNull;

/**
 * Immutable Object
 */
public class TicketTypeRequest {
    @NotNull
    private final int noOfTickets;
    @NotNull
    private final Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT, CHILD, INFANT
    }
}