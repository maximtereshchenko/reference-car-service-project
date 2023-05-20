package com.andersenlab.extension;

import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;

import java.lang.reflect.Method;
import java.util.Comparator;

public final class NaturalMethodOrderer implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(new NaturalOrderComparator());
    }

    private static final class NaturalOrderComparator implements Comparator<MethodDescriptor> {

        @Override
        public int compare(MethodDescriptor methodDescriptor1, MethodDescriptor methodDescriptor2) {
            for (Method method : methodDescriptor1.getMethod().getDeclaringClass().getDeclaredMethods()) {
                if (method.equals(methodDescriptor1.getMethod())) {
                    return -1;
                }
                if (method.equals(methodDescriptor2.getMethod())) {
                    return 1;
                }
            }
            return 0;
        }
    }
}
