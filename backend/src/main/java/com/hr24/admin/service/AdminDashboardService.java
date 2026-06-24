package com.hr24.admin.service;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hr24.admin.dto.AdminHrEmployeeFlowResponseDto;
import com.hr24.admin.dto.EmployeeFlowMonthlyDto;
import com.hr24.admin.dto.EmployeeFlowSummaryDto;
import com.hr24.admin.repository.AdminDashboardRepository;
import com.hr24.admin.repository.AdminDashboardRepository.MonthlyCountRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private static final String ALL = "ALL";
    private static final String REGULAR = "REGULAR";
    private static final String DAILY = "DAILY";

    private final AdminDashboardRepository adminDashboardRepository;

    public AdminHrEmployeeFlowResponseDto getHrEmployeeFlow() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);

        LocalDateTime startDate = startMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        Map<String, Long> activeCounts =
                adminDashboardRepository.countActiveEmployeesByEmploymentType();

        List<MonthlyCountRow> joinRows =
                adminDashboardRepository.countMonthlyJoinByEmploymentType(startDate, endDate);

        List<MonthlyCountRow> leaveRows =
                adminDashboardRepository.countMonthlyLeaveByEmploymentType(startDate, endDate);

        Map<String, MonthlyAccumulator> monthlyMap = createEmptyMonthlyMap(startMonth);

        applyJoinCounts(monthlyMap, joinRows);
        applyLeaveCounts(monthlyMap, leaveRows);

        List<EmployeeFlowMonthlyDto> monthlyDtos = monthlyMap.entrySet()
                .stream()
                .map(entry -> {
                    String month = entry.getKey();
                    MonthlyAccumulator value = entry.getValue();

                    return new EmployeeFlowMonthlyDto(
                            month,
                            createMonthLabel(month),
                            value.regularJoinCount,
                            value.regularLeaveCount,
                            value.dailyJoinCount,
                            value.dailyLeaveCount
                    );
                })
                .toList();

        MonthlyAccumulator currentMonthValue =
                monthlyMap.getOrDefault(currentMonth.toString(), new MonthlyAccumulator());

        Map<String, EmployeeFlowSummaryDto> summary = createSummary(
                activeCounts,
                currentMonthValue
        );

        return new AdminHrEmployeeFlowResponseDto(summary, monthlyDtos);
    }

    private Map<String, MonthlyAccumulator> createEmptyMonthlyMap(YearMonth startMonth) {
        Map<String, MonthlyAccumulator> monthlyMap = new LinkedHashMap<>();

        for (int i = 0; i < 6; i++) {
            YearMonth month = startMonth.plusMonths(i);
            monthlyMap.put(month.toString(), new MonthlyAccumulator());
        }

        return monthlyMap;
    }

    private void applyJoinCounts(
            Map<String, MonthlyAccumulator> monthlyMap,
            List<MonthlyCountRow> rows
    ) {
        for (MonthlyCountRow row : rows) {
            MonthlyAccumulator accumulator = monthlyMap.get(row.month());

            if (accumulator == null) {
                continue;
            }

            if (REGULAR.equals(row.employmentType())) {
                accumulator.regularJoinCount = row.count();
            }

            if (DAILY.equals(row.employmentType())) {
                accumulator.dailyJoinCount = row.count();
            }
        }
    }

    private void applyLeaveCounts(
            Map<String, MonthlyAccumulator> monthlyMap,
            List<MonthlyCountRow> rows
    ) {
        for (MonthlyCountRow row : rows) {
            MonthlyAccumulator accumulator = monthlyMap.get(row.month());

            if (accumulator == null) {
                continue;
            }

            if (REGULAR.equals(row.employmentType())) {
                accumulator.regularLeaveCount = row.count();
            }

            if (DAILY.equals(row.employmentType())) {
                accumulator.dailyLeaveCount = row.count();
            }
        }
    }

    private Map<String, EmployeeFlowSummaryDto> createSummary(
            Map<String, Long> activeCounts,
            MonthlyAccumulator currentMonthValue
    ) {
        long regularActiveCount = getCount(activeCounts, REGULAR);
        long dailyActiveCount = getCount(activeCounts, DAILY);

        long regularJoinCount = currentMonthValue.regularJoinCount;
        long dailyJoinCount = currentMonthValue.dailyJoinCount;

        long regularLeaveCount = currentMonthValue.regularLeaveCount;
        long dailyLeaveCount = currentMonthValue.dailyLeaveCount;

        Map<String, EmployeeFlowSummaryDto> summary = new LinkedHashMap<>();

        summary.put(
                ALL,
                new EmployeeFlowSummaryDto(
                        regularActiveCount + dailyActiveCount,
                        regularJoinCount + dailyJoinCount,
                        regularLeaveCount + dailyLeaveCount
                )
        );

        summary.put(
                REGULAR,
                new EmployeeFlowSummaryDto(
                        regularActiveCount,
                        regularJoinCount,
                        regularLeaveCount
                )
        );

        summary.put(
                DAILY,
                new EmployeeFlowSummaryDto(
                        dailyActiveCount,
                        dailyJoinCount,
                        dailyLeaveCount
                )
        );

        return summary;
    }

    private long getCount(Map<String, Long> counts, String key) {
        return counts.getOrDefault(key, 0L);
    }

    private String createMonthLabel(String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        return yearMonth.getMonthValue() + "월";
    }

    private static class MonthlyAccumulator {
        private long regularJoinCount;
        private long regularLeaveCount;
        private long dailyJoinCount;
        private long dailyLeaveCount;
    }
}
