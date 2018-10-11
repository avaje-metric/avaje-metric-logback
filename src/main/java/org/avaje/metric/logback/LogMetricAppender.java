package org.avaje.metric.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.avaje.metric.CounterMetric;
import org.avaje.metric.MetricManager;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

/**
 * Appender that counts errors and warnings.
 */
public final class LogMetricAppender extends AppenderBase<ILoggingEvent> {

  /**
   * Register the LogMetricAppender to the ROOT logger.
   */
  public static void register() {

    LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);

    LogMetricAppender appender = new LogMetricAppender();
    appender.setContext(root.getLoggerContext());
    appender.start();

    root.addAppender(appender);
  }

  private final CounterMetric errorMetric;

  private final CounterMetric warnMetric;

  /**
   * Create with a given rateUnit.
   */
  public LogMetricAppender() {
    errorMetric = MetricManager.getCounterMetric("app.log.error");
    warnMetric = MetricManager.getCounterMetric("app.log.warn");
  }

  /**
   * Increment the warning or error counters.
   */
  @Override
  protected void append(ILoggingEvent event) {

    switch (event.getLevel().toInt()) {
    case Level.WARN_INT:
      warnMetric.markEvent();
      break;
    case Level.ERROR_INT:
      errorMetric.markEvent();
      break;
    default:
      // not interested in any other logging events
      break;
    }
  }

}
