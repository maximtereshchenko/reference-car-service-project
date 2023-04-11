package com.andersenlab.carservice.application.command;

import com.andersenlab.carservice.port.usecase.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.List;

public final class BusinessExceptionHandlingCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessExceptionHandlingCommand.class);

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
        } catch (GarageSlotIsAssigned e) {
            log(e);
            output.println("Garage slot is assigned");
        } catch (GarageSlotWasNotFound e) {
            log(e);
            output.println("Garage slot was not found");
        } catch (GarageSlotWithSameIdExists e) {
            log(e);
            output.println("Garage slot with same ID exists");
        } catch (OrderHasBeenAlreadyCanceled e) {
            log(e);
            output.println("Order has been already canceled");
        } catch (OrderHasBeenAlreadyCompleted e) {
            log(e);
            output.println("Order has been already completed");
        } catch (OrderHasNoGarageSlotAssigned e) {
            log(e);
            output.println("Order has no garage slot assigned");
        } catch (OrderHasNoRepairersAssigned e) {
            log(e);
            output.println("Order has no repairers assigned");
        } catch (OrderWasNotFound e) {
            log(e);
            output.println("Order was not found");
        } catch (RepairerIsAssigned e) {
            log(e);
            output.println("Repairer is assigned");
        } catch (RepairerWasNotFound e) {
            log(e);
            output.println("Repairer was not found");
        } catch (RepairerWithSameIdExists e) {
            log(e);
            output.println("Repairer with same ID exists");
        }
    }

    private void log(RuntimeException e) {
        LOG.warn("Caught business related exception", e);
    }
}
