package de.cronn.hibernate.stop_guessing_start_testing.test;

import de.cronn.assertions.validationfile.junit5.JUnit5ValidationFileAssertions;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import de.cronn.commons.lang.Action;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface QueryValidationTraits extends JUnit5ValidationFileAssertions {

    String SUFFIX_SQL = "sql";

    QueryAndParamsCapturingListener getQueryListener();

    default void captureQueryAndCompareWithFile(Action action) {
        captureQueryAndCompareWithFile(action.toCallable());
    }

    default <T> T captureQueryAndCompareWithFile(Callable<T> callable) {
        return captureQueryAndCompareWithFile(callable, SUFFIX_SQL);
    }

    default void captureQueryAndCompareWithFile(Action action, String suffix) {
        captureQueryAndCompareWithFile(action.toCallable(), suffix);
    }

    default void captureQueryAndCompareWithFile(Action action, ValidationNormalizer normalizer) {
        captureQueryAndCompareWithFile(action.toCallable(), normalizer);
    }

    default <T> T captureQueryAndCompareWithFile(Callable<T> callable, ValidationNormalizer normalizer) {
        return captureQueryAndCompareUsingAssertionFunction(callable, capturedQueries -> assertWithFileWithSuffix(capturedQueries, normalizer, SUFFIX_SQL));
    }

    default <T> T captureQueryAndCompareWithFile(Callable<T> callable, String suffix) {
        return captureQueryAndCompareWithFile(callable, defaultValidationNormalizerForQueryCapturing(), suffix);
    }

    default ValidationNormalizer defaultValidationNormalizerForQueryCapturing() {
        return ValidationNormalizer.doNothing();
    }

    default <T> T captureQueryAndCompareWithFile(Callable<T> callable, ValidationNormalizer normalizer, String suffix) {
        return captureQueryAndCompareUsingAssertionFunction(callable, capturedQueries -> assertWithFileWithSuffix(capturedQueries, normalizer, suffix));
    }

    private <T> T captureQueryAndCompareUsingAssertionFunction(Callable<T> callable, Consumer<String> assertionFunction) {
        QueryAndParamsCapturingListener queryListener = getQueryListener();
        queryListener.startListening();
        try {
            T result = callable.call();
            String capturedQueries = queryListener.getCapturedQueries().stream().map(QueryValidationTraits::createLogEntry).collect(Collectors.joining("\n\n"));

            assertionFunction.accept(capturedQueries);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            queryListener.stopListening();
        }
    }

    static String createLogEntry(CapturedQuery capturedQuery) {
        return new CustomQueryLogEntryCreator().getLogEntry(capturedQuery.execInfo(), capturedQuery.queryInfoList(), true, false, true);
    }

    class CustomQueryLogEntryCreator extends DefaultQueryLogEntryCreator {

        CustomQueryLogEntryCreator() {
            setMultiline(true);
        }

        @Override
        protected void writeTimeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            /* Time entry changes, we don't like changes in validation files */
            /* Sonar, are you happy now ? */
        }

        @Override
        protected String formatQuery(String query) {
            return FormatStyle.BASIC.getFormatter().format(query);
        }
    }
}
