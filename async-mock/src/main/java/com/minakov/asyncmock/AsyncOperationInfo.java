package com.minakov.asyncmock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsyncOperationInfo {

    private String requestId;
    private String result;

}
