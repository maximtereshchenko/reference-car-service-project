package com.andersenlab.carservice.application;

import com.andersenlab.carservice.domain.CarServiceModule;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Stream;

public final class TextInterface {

    private final InputStream inputStream;
    private final PrintStream printStream;
    private final Collection<Command> commands;

    TextInterface(InputStream inputStream, PrintStream printStream, Command... commands) {
        this(inputStream, printStream, List.of(commands));
    }

    private TextInterface(InputStream inputStream, PrintStream printStream, Collection<? extends Command> commands) {
        this.inputStream = inputStream;
        this.printStream = printStream;
        this.commands = List.copyOf(commands);
    }

    public static TextInterface forModule(CarServiceModule carServiceModule) {
        return new TextInterface(
                System.in,
                System.out,
                allCommands(mainCommands(carServiceModule))
        );
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

    public void run() {
        try (var scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            printStream.println(
                    """
                    Welcome to "Car Service Inc."!
                    """
            );
            while (true) {
                printStream.print('>');
                var input = scanner.nextLine();
                if (input.equals("exit")) {
                    return;
                }
                commands.forEach(command -> command.execute(printStream, List.of(input.trim().split(" "))));
                printStream.println();
            }
        }
    }
}
