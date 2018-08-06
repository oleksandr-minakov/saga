package com.minakov.asyncmock;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

import com.minakov.asyncmock.command.AsyncActionCommand;
import com.minakov.asyncmock.command.CompensateAsyncActionCommand;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncMockCommandHandler {

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("async-mock")
                .onMessage(AsyncActionCommand.class, this::asyncAction)
                .onMessage(CompensateAsyncActionCommand.class, this::compensateAsyncAction)
                .build();
    }

    public Message asyncAction(CommandMessage<AsyncActionCommand> commandMessage) {
        log.info("received AsyncActionCommand");
        AsyncActionCommand command = commandMessage.getCommand();

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            log.error("Something went wrong during async action.", e);
        }
        return withSuccess(new AsyncOperationInfo(command.getVportRequestId(), "ololo"));
    }

    private Message compensateAsyncAction(CommandMessage<CompensateAsyncActionCommand> commandMessage) {
        log.info("received CompensateAsyncActionCommand");
        //do nothing
        return withSuccess();
    }

}
