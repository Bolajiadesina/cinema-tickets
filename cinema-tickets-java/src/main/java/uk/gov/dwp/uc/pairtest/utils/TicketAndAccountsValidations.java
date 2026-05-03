package uk.gov.dwp.uc.pairtest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import uk.gov.dwp.uc.pairtest.domain.TicketSummary;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@Component
@PropertySource("classpath:application.properties")
public class TicketAndAccountsValidations {

    Logger logger = LoggerFactory.getLogger(TicketAndAccountsValidations.class);

    @Value("${MIN_TICKET}")
    private int minTicket;
    @Value("${MAX_TICKET}")
    private int maxTicket;

    public int getMinTicket() {
        return minTicket;
    }

    public int getMaxTicket() {
        return maxTicket;
    }

    public void validateAccountId(Long accountId) {
        logger.info("Validating account ID: " + accountId);
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID.");
        }
    }

    public void validateTicketPurchase(TicketSummary summary) {

        logger.info("Validating ticket purchase: " + summary);
        if (summary.getTotalTickets() < minTicket) {
            throw new InvalidPurchaseException(
                    "At least " + minTicket + " ticket must be purchased.");
        }
        if (summary.getTotalTickets() > maxTicket) {
            throw new InvalidPurchaseException(
                    "Only a maximum of " + maxTicket + " tickets can be purchased at a time.");
        }

        if (summary.getAdultTickets() == 0 && (summary.getChildTickets() > 0 || summary.getInfantTickets() > 0)) {
            throw new InvalidPurchaseException(
                    "Child and Infant tickets cannot be purchased without purchasing an Adult ticket.");
        }

        if (summary.getInfantTickets() > summary.getAdultTickets()) {
            throw new InvalidPurchaseException("Number of INFANT tickets cannot exceed number of ADULT tickets");
        }
    }

}
