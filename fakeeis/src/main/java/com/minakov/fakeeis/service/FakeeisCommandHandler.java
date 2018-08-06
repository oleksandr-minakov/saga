package com.minakov.fakeeis.service;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

import com.minakov.fakeeis.GenericVportInfo;
import com.minakov.fakeeis.command.CompensateCreateGenericVportCommand;
import com.minakov.fakeeis.command.CreateGenericVportCommand;
import com.minakov.fakeeis.entities.GenericVport;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakeeisCommandHandler {

    @Autowired
    private FakeeisService fakeeisService;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("fakeeis")
                .onMessage(CreateGenericVportCommand.class, this::createGenericVport)
                .onMessage(CompensateCreateGenericVportCommand.class, this::compensateGenericVport)
                .build();
    }

    public Message createGenericVport(CommandMessage<CreateGenericVportCommand> commandMessage) {
        log.info("received CreateGenericVportCommand");
        CreateGenericVportCommand command = commandMessage.getCommand();

        GenericVport genericVport = fakeeisService.createGenericVport(command.getVportRequestId(), command.getFoo());
        return withSuccess(new GenericVportInfo(genericVport.getId(), genericVport.getFoo(), genericVport.getStatus().name()));
    }

    private Message compensateGenericVport(CommandMessage<CompensateCreateGenericVportCommand> commandMessage) {
        log.info("received CompensateCreateGenericVportCommand");
        CompensateCreateGenericVportCommand command = commandMessage.getCommand();

        fakeeisService.deleteGenericVport(command.getVportRequestId());
        return withSuccess();
    }

}
