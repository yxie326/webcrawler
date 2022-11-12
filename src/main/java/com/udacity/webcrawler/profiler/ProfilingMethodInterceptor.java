package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object delegate;
  private final ProfilingState state;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState state) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = Objects.requireNonNull(delegate);
    this.state = state;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    boolean profiled = method.getAnnotation(Profiled.class) != null;
    Instant startTime = null;
    if (profiled) {
      startTime = clock.instant();
    }
    Object result;
    try {
      result = method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      // Not sure how all these work. Copied from Dynamic Proxy Solution.
      throw e.getTargetException();
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } finally {
      if (profiled) {
        Instant endTime = clock.instant();
        Duration elapsed = Duration.between(startTime, endTime);
        state.record(delegate.getClass(), method, elapsed);
      }
    }
    return result;
  }
}
