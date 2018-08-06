package com.minakov.fakeeis.service;

import com.minakov.fakeeis.entities.GenericVport;
import com.minakov.fakeeis.entities.GenericVportStatus;
import com.minakov.fakeeis.repository.GenericVportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class FakeeisService {

    @Autowired
    private GenericVportRepository genericVportRepository;

    public GenericVport createGenericVport(String requestId, String foo) {
        log.info("creating generic vPort - {}", requestId);

        return genericVportRepository.save(new GenericVport(requestId, foo, GenericVportStatus.DEPLOYED));
    }

    @Transactional
    public void deleteGenericVport(String requestId) {
        log.info("deleting generic vPort - {}", requestId);

        GenericVport genericVport = genericVportRepository.findByRequestId(requestId);
        genericVportRepository.deleteById(genericVport.getId());
        log.info("generic vPort {} deleted", genericVport);
    }
}
