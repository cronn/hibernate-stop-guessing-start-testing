package de.cronn.hibernate.stop_guessing_start_testing.test;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

public record CapturedQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
}
