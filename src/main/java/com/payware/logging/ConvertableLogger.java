package com.payware.logging;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class ConvertableLogger implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    Stream.of(methodInvocation.getArguments()).forEach(arg -> log.info(String.valueOf(arg)));
    return methodInvocation.proceed();
  }
}
