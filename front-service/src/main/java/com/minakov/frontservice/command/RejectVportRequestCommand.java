package com.minakov.frontservice.command;

import com.minakov.frontservice.saga.VportSagaData;
import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectVportRequestCommand implements Command {

    private VportSagaData vportSagaData;
}
