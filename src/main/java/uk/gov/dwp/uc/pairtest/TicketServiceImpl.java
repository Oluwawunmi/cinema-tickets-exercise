package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketRequestNums;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


public class TicketServiceImpl implements TicketService {

    //business rules
    //private static final int INFANT_TICKET_PRICE = 0;
    private static final int CHILD_TICKET_PRICE = 10;
    private static final int ADULT_TICKET_PRICE = 20;
    private static final int MAX_TICKET_ALLOWED = 20;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(TicketPurchaseRequest ticketPurchaseRequest) throws InvalidPurchaseException {

        //account validation
        if(ticketPurchaseRequest.getAccountId() <= 0){
            throw new InvalidPurchaseException("Invalid Account ID.");
        }

        //get Ticket Numbers for the different types
        TicketRequestNums ticketRequestNums = getTicketRequestNumbers(ticketPurchaseRequest.getTicketTypeRequests());

        //ticket request and type validation
        validateTicketRequest(ticketRequestNums);

        //calculate total ticket amount and make payment
        ticketPaymentService.makePayment(ticketPurchaseRequest.getAccountId(), calculateTotalTicketAmount(ticketRequestNums));

        //calculate no of seats to reserve and make reservation
        seatReservationService.reserveSeat(ticketPurchaseRequest.getAccountId(), ticketRequestNums.getAdultTicketsNum() + ticketRequestNums.getChildTicketsNum());
    }

    private TicketRequestNums getTicketRequestNumbers(TicketRequest[] ticketRequests){
        TicketRequestNums ticketRequestNums = new TicketRequestNums();
        for (TicketRequest ticketRequest : ticketRequests){
            if (ticketRequest.getTicketType().equals(TicketRequest.Type.ADULT)){
                ticketRequestNums.setAdultTicketsNum(ticketRequest.getNoOfTickets());
            }else if (ticketRequest.getTicketType().equals(TicketRequest.Type.CHILD)){
                ticketRequestNums.setChildTicketsNum(ticketRequest.getNoOfTickets());
            }else {
                ticketRequestNums.setInfantTicketsNum(ticketRequest.getNoOfTickets());
            }
        }
        return  ticketRequestNums;
    }

    private void validateTicketRequest(TicketRequestNums ticketRequestNums){

        if (ticketRequestNums.getAdultTicketsNum() <= 0 || ticketRequestNums.getAdultTicketsNum() > MAX_TICKET_ALLOWED){
            throw new InvalidPurchaseException("Invalid number of adult tickets.");
        }

        if (ticketRequestNums.getChildTicketsNum() < 0 || ticketRequestNums.getChildTicketsNum() > MAX_TICKET_ALLOWED){
            throw new InvalidPurchaseException("Invalid number of child tickets.");
        }

        if (ticketRequestNums.getInfantTicketsNum() < 0 || ticketRequestNums.getInfantTicketsNum() > MAX_TICKET_ALLOWED){
            throw new InvalidPurchaseException("Invalid number of infact tickets.");
        }

        if (ticketRequestNums.getTotalTicketsNum() <= 0 || ticketRequestNums.getTotalTicketsNum() > MAX_TICKET_ALLOWED){
            throw new InvalidPurchaseException("Invalid total number of tickets.");
        }

        if (ticketRequestNums.getInfantTicketsNum() > ticketRequestNums.getAdultTicketsNum()){
            throw new InvalidPurchaseException("Infant tickets cannot exceed adult tickets.");
        }

    }

    private int calculateTotalTicketAmount(TicketRequestNums ticketRequestNums){
        return (ticketRequestNums.getAdultTicketsNum() * ADULT_TICKET_PRICE) + (ticketRequestNums.getChildTicketsNum() * CHILD_TICKET_PRICE);
    }

}
