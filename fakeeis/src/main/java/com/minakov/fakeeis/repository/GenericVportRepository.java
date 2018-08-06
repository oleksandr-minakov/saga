package com.minakov.fakeeis.repository;


import com.minakov.fakeeis.entities.GenericVport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericVportRepository extends JpaRepository<GenericVport, String> {

    GenericVport findByRequestId(String requestId);

}
