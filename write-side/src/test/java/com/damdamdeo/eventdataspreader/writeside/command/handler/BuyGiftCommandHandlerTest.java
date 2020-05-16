package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.DefaultAggregateRootRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@QuarkusTest
public class BuyGiftCommandHandlerTest extends AbstractCommandHandlerTest {

    @Inject
    BuyGiftCommandHandler buyGiftCommandHandler;

    @InjectMock
    DefaultAggregateRootRepository mockDefaultAggregateRootRepository;

    @InjectMock
    GiftAggregateRootProvider giftAggregateRootProvider;

    @Test
    public void should_buy_gift() throws Throwable {
        // Given
        final BuyGiftCommand buyGiftCommand = new BuyGiftCommand("lapinou", "damdamdeo");
        final GiftAggregate giftAggregate = mock(GiftAggregate.class);
        doReturn(giftAggregate).when(giftAggregateRootProvider).create();
        when(mockDefaultAggregateRootRepository.save(any())).then(returnsFirstArg());

        // When
        final GiftAggregate giftAggregateCreated = buyGiftCommandHandler.execute(buyGiftCommand);

        // Then
        verify(giftAggregate, times(1)).handle(buyGiftCommand);
        assertEquals(giftAggregateCreated, giftAggregate);
        verify(giftAggregateRootProvider, times(1)).create();
        verify(mockDefaultAggregateRootRepository, times(1)).save(any());

        verify(spyCommandExecutor, times(1)).execute(any());
        verifyNoMoreInteractions(mockDefaultAggregateRootRepository, spyCommandExecutor, giftAggregateRootProvider);
    }

}
