package com.hr24.admin.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminDashboardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Map<String, Long> countActiveEmployeesByEmploymentType() {
        String sql = """
                SELECT employment_type,
                       COUNT(*) AS count_value
                FROM users
                WHERE status = 'ACTIVE'
                GROUP BY employment_type
                """;

        return jdbcTemplate.query(sql, rs -> {
            Map<String, Long> result = new HashMap<>();

            while (rs.next()) {
                result.put(
                        rs.getString("employment_type"),
                        rs.getLong("count_value")
                );
            }

            return result;
        });
    }

    public List<MonthlyCountRow> countMonthlyJoinByEmploymentType(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String sql = """
                SELECT TO_CHAR(hire_date, 'YYYY-MM') AS month_value,
                       employment_type,
                       COUNT(*) AS count_value
                FROM users
                WHERE hire_date >= :startDate
                  AND hire_date < :endDate
                GROUP BY TO_CHAR(hire_date, 'YYYY-MM'), employment_type
                ORDER BY month_value, employment_type
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("startDate", Timestamp.valueOf(startDate))
                .addValue("endDate", Timestamp.valueOf(endDate));

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new MonthlyCountRow(
                        rs.getString("month_value"),
                        rs.getString("employment_type"),
                        rs.getLong("count_value")
                )
        );
    }

    public List<MonthlyCountRow> countMonthlyLeaveByEmploymentType(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        String sql = """
                SELECT TO_CHAR(resignation_date, 'YYYY-MM') AS month_value,
                       employment_type,
                       COUNT(*) AS count_value
                FROM users
                WHERE resignation_date >= :startDate
                  AND resignation_date < :endDate
                GROUP BY TO_CHAR(resignation_date, 'YYYY-MM'), employment_type
                ORDER BY month_value, employment_type
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("startDate", Timestamp.valueOf(startDate))
                .addValue("endDate", Timestamp.valueOf(endDate));

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new MonthlyCountRow(
                        rs.getString("month_value"),
                        rs.getString("employment_type"),
                        rs.getLong("count_value")
                )
        );
    }

    public record MonthlyCountRow(
            String month,
            String employmentType,
            long count
    ) {
    }
}
