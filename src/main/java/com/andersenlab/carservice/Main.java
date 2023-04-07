package com.andersenlab.carservice;

import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.application.TextInterface;
import com.andersenlab.carservice.application.command.*;
import com.andersenlab.carservice.domain.CarServiceModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Main {

    public static void main(String[] args) {
        var module = new CarServiceModule(new InMemoryRepairerStore());
        var mainCommands = List.of(
                new CompositeCommand(
                        "repairers",
                        new AddRepairer(module.addRepairerUseCase()),
                        new ListRepairersCommand(module.listRepairersUserCase()),
                        new DeleteRepairer(module.deleteRepairerUseCase())
                )
        );
        new TextInterface(
                System.in,
                System.out,
                allCommands(mainCommands)
        )
                .run();
    }

    private static Collection<Command> allCommands(List<CompositeCommand> mainCommands) {
        var all = new ArrayList<Command>(mainCommands);
        all.add(new HelpCommand(mainCommands));
        return all;
    }
}
