package de.cronn.hibernate.stop_guessing_start_testing.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.NoOpQueryExecutionListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryAndParamsCapturingListener extends NoOpQueryExecutionListener {

    private boolean listening;

    private final List<CapturedQuery> capturedQueries = new ArrayList<>();

    List<CapturedQuery> getCapturedQueries() {
        return capturedQueries;
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        if (!listening) {
            return;
        }

        capturedQueries.add(new CapturedQuery(execInfo, queryInfoList));
    }

    void startListening() {
        listening = true;
    }

    void stopListening() {
        listening = false;
        capturedQueries.clear();
    }
}
