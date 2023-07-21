import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.dwp.uc.pairtest.domain.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.*;

public class TicketServiceImplTest{

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPurchaseTickets_ValidRequest_SuccessfulPaymentAndReservation() throws InvalidPurchaseException {
        TicketRequest ticketRequest1 = new TicketRequest(TicketRequest.Type.ADULT, 2);
        TicketRequest ticketRequest2 = new TicketRequest(TicketRequest.Type.CHILD, 1);
        TicketRequest ticketRequest3 = new TicketRequest(TicketRequest.Type.INFANT, 1);
        TicketRequest[] ticketRequests = {ticketRequest1, ticketRequest2, ticketRequest3};

        TicketPurchaseRequest ticketPurchaseRequest = new TicketPurchaseRequest(2L, ticketRequests); // Example: 2 adult, 1 child, 1 infant

        ticketService.purchaseTickets(ticketPurchaseRequest);

        // Verify that paymentService.makePayment is called with the correct total amount
        verify(paymentService, times(1)).makePayment(2L, 50); // Assuming 2 adult tickets cost 40.0 and 1 child ticket costs 10.0

        // Verify that reservationService.reserveSeats is called with the correct number of seats
        verify(reservationService, times(1)).reserveSeat(2L, 3); // 2 adult tickets + 1 child ticket = 3 seats
    }

    @Test
    public void testPurchaseTickets_InvalidChildTickets_ThrowsException() {
        TicketRequest ticketRequest1 = new TicketRequest(TicketRequest.Type.ADULT, 3);
        TicketRequest ticketRequest2 = new TicketRequest(TicketRequest.Type.CHILD, -1);
        TicketRequest ticketRequest3 = new TicketRequest(TicketRequest.Type.INFANT, 1);
        TicketRequest[] ticketRequests = {ticketRequest1, ticketRequest2, ticketRequest3};

        TicketPurchaseRequest ticketPurchaseRequest = new TicketPurchaseRequest(2L, ticketRequests); // Example: 3 adult, -1 child, 1 infant

        // When the purchaseTickets method is called with an invalid ticket purchase request, it should throw an exception
        InvalidPurchaseException exception = org.junit.jupiter.api.Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(ticketPurchaseRequest);
        });

        // If an exception is thrown, there should be no interactions with the paymentService or reservationService
        verifyNoInteractions(paymentService);
        verifyNoInteractions(reservationService);

        // To check the exception message or other properties if needed
        org.junit.jupiter.api.Assertions.assertEquals("Invalid number of child tickets.", exception.getMessage());
    }

}
