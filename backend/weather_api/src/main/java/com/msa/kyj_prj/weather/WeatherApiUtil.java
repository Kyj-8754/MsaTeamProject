package com.msa.kyj_prj.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherApiUtil {

    private static final String SERVICE_KEY = "hhJrtCIbQi3NSeHNduSiz4vmFmSGrSO6GWLdls+pc3Ia0rIavAg472tL3bkf4c73d5rinqBCoplCiseQsMZoOg==";
    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    public static List<Weather> fetchForecast(String x, String y, String regionName) {
    	
        int[] grid = convertGPS2Grid(Double.parseDouble(x), Double.parseDouble(y));
        String nx = String.valueOf(grid[0]);
        String ny = String.valueOf(grid[1]);
    	
        // 날짜 및 기준시각 계산 (1일 8회 기준시각)
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String baseTime = getBaseTime(hour);
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        System.out.println("▶▶ [" + regionName + "] 호출 시작");
        String json = callApi(nx, ny, baseDate, baseTime);
        System.out.println("▶▶ 응답 길이: " + (json != null ? json.length() : "null"));
        
        return parseWeatherJson(json, regionName);
    }

    private static String callApi(String x, String y, String baseDate, String baseTime) {
        try {
            OkHttpClient client = new OkHttpClient();
            String encodedKey = URLEncoder.encode(SERVICE_KEY, "UTF-8");

            String url = BASE_URL + "?" +
                    "serviceKey=" + encodedKey +
                    "&numOfRows=1000&pageNo=1" +
                    "&dataType=JSON" +
                    "&base_date=" + baseDate +
                    "&base_time=" + baseTime +
                    "&nx=" + x +
                    "&ny=" + y;

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            System.out.println("▶▶ 호출 URL: " + url);
            return response.body() != null ? response.body().string() : null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Weather> parseWeatherJson(String json, String regionName) {
        List<Weather> weatherList = new ArrayList<>();
        if (json == null || json.isEmpty()) return weatherList;

        // ✅ 저장할 코드 목록 정의
        Set<String> allowedCategories = Set.of("POP", "PTY", "PCP", "REH", "SNO", "SKY", "TMP", "WSD");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(json)
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            for (JsonNode item : items) {
                String category = item.path("category").asText();
                
                // ✅ 필터링
                if (!allowedCategories.contains(category)) continue;

                Weather w = new Weather();
                w.setWeather_base_date(item.path("baseDate").asText());
                w.setWeather_base_time(item.path("baseTime").asText());
                w.setWeather_fcst_date(item.path("fcstDate").asText());
                w.setWeather_fcst_time(item.path("fcstTime").asText());
                w.setWeather_code(category);
                w.setWeather_value(item.path("fcstValue").asText());
                w.setWeather_region(regionName);
                weatherList.add(w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherList;
    }


    private static String getBaseTime(int hour) {
        // 기준 발표 시각 (단기예보 8회 발표 기준)
        int[] baseTimes = {2, 5, 8, 11, 14, 17, 20, 23};

        // 가장 가까운 과거 기준시각 찾기
        for (int i = baseTimes.length - 1; i >= 0; i--) {
            if (hour >= baseTimes[i]) {
                return String.format("%02d00", baseTimes[i]);
            }
        }

        // 자정 이전 (0시~1시) → 전날 23시 예보 사용
        return "2300";
    }
    
    private static int[] convertGPS2Grid(double lon, double lat) {
        double RE = 6371.00877; // Earth radius (km)
        double GRID = 5.0; // Grid spacing (km)
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        return new int[]{nx, ny};
    }
}
