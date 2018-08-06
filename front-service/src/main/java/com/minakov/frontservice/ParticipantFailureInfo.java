package com.minakov.frontservice;

import lombok.Data;

@Data
public class ParticipantFailureInfo {

    private String id;
    private String requestVportId;
    private String cause;

}
