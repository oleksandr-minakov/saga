package com.minakov.asyncmock.command;

import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompensateAsyncActionCommand implements Command {

    private String vportRequestId;
    private String foo;

}

