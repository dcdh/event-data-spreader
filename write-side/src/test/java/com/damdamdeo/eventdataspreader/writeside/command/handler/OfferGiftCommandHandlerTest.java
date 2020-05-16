package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.DefaultAggregateRootRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@QuarkusTest
public class OfferGiftCommandHandlerTest extends AbstractCommandHandlerTest {

    @Inject
    OfferGiftCommandHandler offerGiftCommandHandler;

    @InjectMock
    DefaultAggregateRootRepository mockDefaultAggregateRootRepository;

    @Test
    public void should_offer_gift() throws Throwable {
        // Given
        final OfferGiftCommand offerGiftCommand = new OfferGiftCommand("lapinou", "damdamdeo", "damdamdeo");
        final GiftAggregate giftAggregate = mock(GiftAggregate.class);
        when(mockDefaultAggregateRootRepository.save(giftAggregate)).then(returnsFirstArg());
        doReturn(giftAggregate).when(mockDefaultAggregateRootRepository).load("lapinou", GiftAggregate.class);

        // When
        final GiftAggregate giftAggregateOffered = offerGiftCommandHandler.execute(offerGiftCommand);

        // Then
        verify(giftAggregate, times(1)).handle(offerGiftCommand);

        assertEquals(giftAggregateOffered, giftAggregate);
        verify(mockDefaultAggregateRootRepository, times(1)).save(any());
        verify(mockDefaultAggregateRootRepository, times(1)).load(any(), any());
        verify(spyCommandExecutor, times(1)).execute(any());
        verifyNoMoreInteractions(mockDefaultAggregateRootRepository, spyCommandExecutor);
    }

}
