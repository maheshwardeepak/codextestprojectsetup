package com.minitaskflow.dashboard;

public record DashboardSummaryResponse(long projectCount, long taskCount, long completedTaskCount) {
}
