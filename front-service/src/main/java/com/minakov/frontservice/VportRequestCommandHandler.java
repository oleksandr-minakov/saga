package com.minakov.frontservice;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

import com.minakov.frontservice.command.CompleteVportRequestCommand;
import com.minakov.frontservice.command.RejectVportRequestCommand;
import com.minakov.frontservice.entities.VportRequest;
import com.minakov.frontservice.entities.VportRequestStatus;
import com.minakov.frontservice.repositories.VportRequestRepository;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VportRequestCommandHandler {

    @Autowired
    private VportRequestRepository vportRequestRepository;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("frontService")
                .onMessage(RejectVportRequestCommand.class, this::rejectVportRequest)
                .onMessage(CompleteVportRequestCommand.class, this::completeVportRequest)
                .build();
    }

    public Message rejectVportRequest(CommandMessage<RejectVportRequestCommand> commandMessage) {
        log.info("received RejectVportRequestCommand");
        RejectVportRequestCommand command = commandMessage.getCommand();

        vportRequestRepository.deleteById(command.getVportSagaData().getId());

        log.info(String.format("vportRequest %s successfully rejected", command.getVportSagaData().getId()));
        return withSuccess();
    }

    public Message completeVportRequest(CommandMessage<CompleteVportRequestCommand> commandMessage) {
        log.info("received CompleteVportRequestCommand");
        CompleteVportRequestCommand command = commandMessage.getCommand();

        VportRequest vportRequest =
                vportRequestRepository.findById(command.getVportRequestId()).orElseThrow(RuntimeException::new);
        vportRequest.setStatus(VportRequestStatus.DEPLOYED);
        vportRequestRepository.save(vportRequest);

        log.info(String.format("VportRequest %s fully completed", vportRequest));
        return withSuccess();
    }

}
