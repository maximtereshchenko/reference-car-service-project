package com.andersenlab.carservice;

import com.andersenlab.carservice.application.InMemoryGarageSlotStore;
import com.andersenlab.carservice.application.InMemoryOrderStore;
import com.andersenlab.carservice.application.InMemoryRepairerStore;
import com.andersenlab.carservice.application.TextInterface;
import com.andersenlab.carservice.application.command.*;
import com.andersenlab.carservice.domain.CarServiceModule;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

final class Main {

    public static void main(String[] args) {
        var module = new CarServiceModule(
                new InMemoryRepairerStore(),
                new InMemoryGarageSlotStore(),
                new InMemoryOrderStore(),
                Clock.systemDefaultZone()
        );
        var mainCommands = List.of(
                new CompositeCommand(
                        "repairers",
                        new AddRepairer(module.addRepairerUseCase()),
                        new ListRepairers(module.listRepairersUserCase()),
                        new DeleteRepairer(module.deleteRepairerUseCase())
                ),
                new CompositeCommand(
                        "garage-slots",
                        new AddGarageSlot(module.addGarageSlotUseCase()),
                        new ListGarageSlots(module.listGarageSlotsUseCase()),
                        new DeleteGarageSlot(module.deleteGarageSlotUseCase())
                ),
                new CompositeCommand(
                        "orders",
                        new CreateOrder(module.createOrderUseCase()),
                        new ListOrders(module.listOrdersUseCase()),
                        new CompositeCommand(
                                "assign",
                                new AssignGarageSlotToOrder(module.assignGarageSlotToOrderUseCase())
                        ),
                        new ViewOrder(module.viewOrderUseCase())
                )
        );
        new TextInterface(
                System.in,
                System.out,
                allCommands(mainCommands)
        )
                .run();
    }

    private static Collection<? extends Command> allCommands(List<? extends Command> mainCommands) {
        return Stream.concat(
                        mainCommands.stream(),
                        Stream.of(new Help(mainCommands))
                )
                .map(GenericExceptionHandlingCommand::new)
                .toList();
    }
}
