package com.minakov.fakeeis.entities;

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


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "GenericVport")
public class GenericVport {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "genericVportId")
    private String id;

    @Column(name = "requestId", nullable = false)
    private String requestId;

    @Column(name = "foo", nullable = false)
    private String foo;

    @Column(name = "status", nullable = false, columnDefinition = "enum('DEPLOYING', 'DEPLOYED', 'FAILED', 'DELETED')")
    @Enumerated(value = EnumType.STRING)
    private GenericVportStatus status;

    public GenericVport(String requestId, String foo, GenericVportStatus status) {
        this.requestId = requestId;
        this.foo = foo;
        this.status = status;
    }
}
