package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.exception.*;

import java.io.PrintStream;
import java.util.List;

public final class BusinessExceptionHandlingCommand implements Command {

    private final Command original;

    public BusinessExceptionHandlingCommand(Command original) {
        this.original = original;
    }

    @Override
    public void printDescription(PrintStream output) {
        original.printDescription(output);
    }

    @Override
    public void execute(PrintStream output, List<String> arguments) {
        try {
            original.execute(output, arguments);
        } catch (GarageSlotWasNotFound e) {
            output.println("Garage slot was not found");
        } catch (OrderWasNotFound e) {
            output.println("Order was not found");
        } catch (RepairerWasNotFound e) {
            output.println("Repairer was not found");
        } catch (OrderHasNoGarageSlotAssigned e) {
            output.println("Order has no garage slot assigned");
        } catch (OrderHasNoRepairersAssigned e) {
            output.println("Order has no repairers assigned");
        } catch (OrderHasBeenAlreadyCompleted e) {
            output.println("Order has been already completed");
        }
    }
}
