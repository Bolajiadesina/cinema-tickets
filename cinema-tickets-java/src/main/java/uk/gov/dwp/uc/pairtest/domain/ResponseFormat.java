package uk.gov.dwp.uc.pairtest.domain;


public enum ResponseFormat {
    INVALID_TICKET_COUNT;

    public String responseMessage(Integer minTicket, Integer maxTicket) {
        return "Number of tickets must be between " + minTicket + " and " + maxTicket;
    }

    String responseMessage() {
        return this.name();
    }
}
