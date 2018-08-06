package com.minakov.frontservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "VportRequest")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class VportRequest {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @Column(name = "foo", nullable = false)
    private String foo;

    @Column(name = "status", nullable = false, columnDefinition = "enum('DEPLOYING', 'DEPLOYED', 'FAILED', 'DELETED')")
    @Enumerated(value = EnumType.STRING)
    private VportRequestStatus status;

    @Column(name = "genericVportId")
    private String genericVport;

}
