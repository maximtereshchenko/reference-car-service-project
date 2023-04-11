package com.andersenlab;

import com.andersenlab.carservice.application.TextInterface;
import com.andersenlab.carservice.application.command.*;
import com.andersenlab.carservice.application.storage.OnDiskGarageSlotStore;
import com.andersenlab.carservice.application.storage.OnDiskOrderStore;
import com.andersenlab.carservice.application.storage.OnDiskRepairerStore;
import com.andersenlab.carservice.application.storage.StateFile;
import com.andersenlab.carservice.domain.CarServiceModule;

import java.nio.file.Paths;
import java.time.Clock;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

final class Main {

    public static void main(String[] args) {
        new TextInterface(
                System.in,
                System.out,
                allCommands(mainCommands(module()))
        )
                .run();
    }

    private static CarServiceModule module() {
        var stateFile = new StateFile(Paths.get("state.json"));
        return new CarServiceModule.Builder()
                .withRepairerStore(new OnDiskRepairerStore(stateFile))
                .withGarageSlotStore(new OnDiskGarageSlotStore(stateFile))
                .withOrderStore(new OnDiskOrderStore(stateFile))
                .withClock(Clock.systemDefaultZone())
                .build();
    }

    private static Collection<Command> mainCommands(CarServiceModule module) {
        return Stream.of(
                        allPrefixed(
                                "repairers",
                                new AddRepairer(module.addRepairerUseCase()),
                                new ListRepairers(module.listRepairersUserCase()),
                                new DeleteRepairer(module.deleteRepairerUseCase())
                        ),
                        allPrefixed(
                                "garage-slots",
                                new AddGarageSlot(module.addGarageSlotUseCase()),
                                new ListGarageSlots(module.listGarageSlotsUseCase()),
                                new DeleteGarageSlot(module.deleteGarageSlotUseCase())
                        ),
                        allPrefixed(
                                "orders",
                                new CreateOrder(module.createOrderUseCase()),
                                new ListOrders(module.listOrdersUseCase()),
                                new PrefixedCommand(
                                        "assign",
                                        new AssignGarageSlotToOrder(module.assignGarageSlotToOrderUseCase())
                                ),
                                new PrefixedCommand(
                                        "assign",
                                        new AssignRepairerToOrder(module.assignRepairerToOrderUseCase())
                                ),
                                new ViewOrder(module.viewOrderUseCase()),
                                new CompleteOrder(module.completeOrderUseCase()),
                                new CancelOrder(module.cancelOrderUseCase())
                        )
                )
                .flatMap(Function.identity())
                .toList();
    }

    private static Stream<Command> allPrefixed(String prefix, Command... commands) {
        return Stream.of(commands)
                .map(command -> new PrefixedCommand(prefix, command));
    }

    private static Collection<? extends Command> allCommands(Collection<Command> mainCommands) {
        return Stream.concat(
                        mainCommands.stream(),
                        Stream.of(new Help(mainCommands))
                )
                .map(GenericExceptionHandlingCommand::new)
                .map(BusinessExceptionHandlingCommand::new)
                .toList();
    }
}
