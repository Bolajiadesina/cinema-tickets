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

    @Value("${INVALID_ACCOUNT_ID_MESSAGE}")
    private String invalidAccountIdMessage;

    public int getMinTicket() {
        return minTicket;
    }

    public int getMaxTicket() {
        return maxTicket;
    }

    public void validateAccountId(Long accountId) {
        logger.info("Validating account ID: " + accountId);
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException(invalidAccountIdMessage);
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
                    "VAL_002", // The new error code for monitoring
                    "Cannot purchase more than 25 tickets." // KEEP THIS STRING EXACTLY AS IT IS IN THE TEST
                );
            }

        
        if (summary.getAdultTickets() == 0 && (summary.getChildTickets() > 0 || summary.getInfantTickets() > 0)) {
    throw new InvalidPurchaseException(
            "VAL_003", 
            "Child and Infant tickets cannot be purchased without purchasing an Adult ticket."
    );
}

        if (summary.getInfantTickets() > summary.getAdultTickets()) {
            throw new InvalidPurchaseException("Number of INFANT tickets cannot exceed number of ADULT tickets");
        }
    }

}
