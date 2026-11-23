package com.festivaltap.core.ports.in;

import com.festivaltap.core.model.TapUsageReport;
import com.festivaltap.core.ports.in.command.FetchUsageReportCommand;

/**
 * Inbound port for materializing a read-only usage and spending report by replaying a tap's event stream.
 */
public interface TapUsageReportFetchingUseCase {

    /**
     * Reconstructs the tap timeline from its event stream and returns aggregated usage sessions and total spending.
     */
    TapUsageReport fetchReportFor(FetchUsageReportCommand command);
}
