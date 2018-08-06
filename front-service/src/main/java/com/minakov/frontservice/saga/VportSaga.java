package com.minakov.frontservice.saga;

import static com.minakov.frontservice.entities.VportRequestStatus.DEPLOYED;
import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import com.minakov.asyncmock.AsyncOperationInfo;
import com.minakov.asyncmock.command.AsyncActionCommand;
import com.minakov.asyncmock.command.CompensateAsyncActionCommand;
import com.minakov.fakeeis.GenericVportInfo;
import com.minakov.fakeeis.command.CompensateCreateGenericVportCommand;
import com.minakov.fakeeis.command.CreateGenericVportCommand;
import com.minakov.frontservice.command.CompleteVportRequestCommand;
import com.minakov.frontservice.command.RejectVportRequestCommand;
import com.minakov.frontservice.ParticipantFailureInfo;
import com.minakov.frontservice.entities.VportRequest;
import com.minakov.frontservice.repositories.VportRequestRepository;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VportSaga implements SimpleSaga<VportSagaData> {

    @Autowired
    private VportRequestRepository vportRequestRepository;

    private SagaDefinition<VportSagaData> sagaDefinition =
            step()
              .withCompensation(this::rejectSaga)
            .step()
              .invokeParticipant(this::createGenericVport)
                .onReply(GenericVportInfo.class, this::genericVportReply)
                .onReply(ParticipantFailureInfo.class, this::genericVportFailure)
              .withCompensation(this::genericVportCompensate)
            .step()
              .invokeParticipant(this::asyncRequest)
                .onReply(AsyncOperationInfo.class, this::asyncReply)
                .onReply(ParticipantFailureInfo.class, this::asyncFailure)
              .withCompensation(this::asyncOperationCompensate)
            .step()
              .invokeParticipant(this::finishVportRequest)
            .build();

    @Override
    public SagaDefinition<VportSagaData> getSagaDefinition() {
        return sagaDefinition;
    }

    private CommandWithDestination rejectSaga(VportSagaData vportSagaData) {
        log.info("rejectSaga() {}", vportSagaData.getId());

        return send(new RejectVportRequestCommand(vportSagaData))
                .to("frontService")
                .build();
    }

    private CommandWithDestination createGenericVport(VportSagaData vportSagaData) {
        log.info("creatingGenericVport() {}", vportSagaData.getId());

        return send(new CreateGenericVportCommand(vportSagaData.getId(), vportSagaData.getFoo()))
                .to("fakeeis")
                .build();
    }

    private void genericVportReply(VportSagaData vportSagaData, GenericVportInfo genericVportInfo) {
        log.info("genericVportReply() {}", vportSagaData.getId());

        VportRequest vportRequest = vportRequestRepository.findById(vportSagaData.getId()).orElseThrow(RuntimeException::new);
        vportRequest.setGenericVport(genericVportInfo.getId());
        vportRequestRepository.save(vportRequest);
        log.info("vportRequest updated with genericVportId - {}", genericVportInfo.getId());
    }

    private CommandWithDestination genericVportCompensate(VportSagaData vportSagaData) {
        log.info("genericVportCompensate() {}", vportSagaData.getId());

        return send(new CompensateCreateGenericVportCommand(vportSagaData.getId()))
                .to("fakeeis")
                .build();
    }

    private void genericVportFailure(VportSagaData vportSagaData, ParticipantFailureInfo failureInfo) {
        log.info(String.format("genericVportFailure() for %s with cause '%s'", vportSagaData.getId(), failureInfo.getCause()));
    }

    private CommandWithDestination asyncRequest(VportSagaData vportSagaData) {
        log.info("asyncRequest() {}", vportSagaData.getId());

        return send(new AsyncActionCommand(vportSagaData.getId(), vportSagaData.getFoo()))
                .to("async-mock")
                .build();
    }

    private void asyncReply(VportSagaData vportSagaData, AsyncOperationInfo asyncOperationInfo) {
        log.info("asyncReply() asyncOperationInfo {}", asyncOperationInfo);

        VportRequest vportRequest = vportRequestRepository.findById(asyncOperationInfo.getRequestId()).orElseThrow(RuntimeException::new);
        vportRequest.setStatus(DEPLOYED);
        vportRequestRepository.save(vportRequest);
        log.info("Async action was completed");
    }

    private CommandWithDestination asyncOperationCompensate(VportSagaData vportSagaData) {
        log.info("asyncOperationCompensate()");

        return send(new CompensateAsyncActionCommand(vportSagaData.getId(), vportSagaData.getFoo()))
                .to("async-mock")
                .build();
    }

    private void asyncFailure(VportSagaData vportSagaData, ParticipantFailureInfo failureInfo) {
        log.info(String.format("asyncFailure() for %s with cause '%s'", vportSagaData.getId(), failureInfo.getCause()));
    }

    private CommandWithDestination finishVportRequest(VportSagaData vportSagaData) {
        log.info("finishVportRequest()");

        return send(new CompleteVportRequestCommand(vportSagaData.getId()))
               .to("frontService")
               .build();
    }
}
