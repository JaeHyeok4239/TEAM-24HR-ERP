package com.hr24.work.schedule.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.work.schedule.entity.Holiday;
import com.hr24.work.schedule.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayFetchService {

    private final HolidayRepository holidayRepository;
    private final ObjectMapper objectMapper;

    @Value("${holiday.api.key}")
    private String apiKey;

    private static final String API_URL =
        "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public int fetchAndSave(int year) {
        holidayRepository.deleteByHolidayYear(year);

        List<Holiday> holidays = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (int month = 1; month <= 12; month++) {
            String url = API_URL
                    + "?ServiceKey=" + apiKey
                    + "&solYear=" + year
                    + "&solMonth=" + String.format("%02d", month)
                    + "&_type=json&numOfRows=50";

            try {
                String json = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(json);
                JsonNode items = root.path("response").path("body").path("items");

                if (items.isMissingNode() || items.isNull()) continue;

                JsonNode item = items.path("item");

                // 해당 월 공휴일이 1개면 object, 여러 개면 array로 옴
                List<JsonNode> itemList = new ArrayList<>();
                if (item.isArray()) {
                    item.forEach(itemList::add);
                } else if (!item.isMissingNode()) {
                    itemList.add(item);
                }

                for (JsonNode node : itemList) {
                    String isHoliday = node.path("isHoliday").asText();
                    if (!"Y".equals(isHoliday)) continue;

                    String locdateStr = String.valueOf(node.path("locdate").asLong());
                    LocalDate date = LocalDate.parse(locdateStr, DATE_FMT);
                    String dateName = node.path("dateName").asText();
                    int isSubstitute = dateName.contains("대체") ? 1 : 0;

                    holidays.add(Holiday.builder()
                            .holidayYear(year)
                            .holidayDate(date)
                            .holidayName(dateName)
                            .isSubstitute(isSubstitute)
                            .build());
                }
            } catch (Exception e) {
                log.error("{}년 {}월 공휴일 조회 실패: {}", year, month, e.getMessage());
            }
        }

        holidayRepository.saveAll(holidays);
        log.info("{}년 공휴일 {}건 저장 완료", year, holidays.size());
        return holidays.size();
    }
}
