package com.minakov.frontservice.resources;


import com.minakov.frontservice.entities.VportRequest;
import com.minakov.frontservice.entities.VportRequestStatus;
import com.minakov.frontservice.repositories.VportRequestRepository;
import com.minakov.frontservice.saga.VportSagaData;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/private_api/v1/vport")
public class VportRequestResource {

    @Autowired
    private SagaManager<VportSagaData> vportSagaManager;

    @Autowired
    private final VportRequestRepository vportRequestRepository;

    public VportRequestResource(VportRequestRepository vportRequestRepository) {
        this.vportRequestRepository = vportRequestRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    VportRequest createVport(@Valid @RequestParam String foo) {
        log.info("processing vportRequest with foo = {} ", foo);
        VportRequest vportRequest = new VportRequest();
        vportRequest.setFoo(foo);
        vportRequest.setStatus(VportRequestStatus.DEPLOYING);
        vportRequestRepository.saveAndFlush(vportRequest);
        VportSagaData vportSagaData = new VportSagaData(vportRequest.getId(), vportRequest.getFoo());
        vportSagaManager.create(vportSagaData, VportRequest.class, vportRequest.getId());

        return vportRequest;
    }

    @GetMapping("/{vportId}")
    @ResponseBody
    Optional getVport(@PathVariable String vportId) {
        return vportRequestRepository.findById(vportId);
    }

    @DeleteMapping("/{vportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteVport(@PathVariable String vportId) {
        vportRequestRepository.findById(vportId);
    }

}
