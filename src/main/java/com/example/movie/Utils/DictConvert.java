package com.example.movie.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者: 枷
 * 时间:2026/10/6 下午7:55
 * 描述:编码转中文工具类，用于 MovieTools 返回给 AI 的数据转换
 */
public class DictConvert {

    private static final Map<String, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> COUNTRY_MAP = new HashMap<>();
    private static final Map<Integer, String> LANGUAGE_MAP = new HashMap<>();
    private static final Map<Integer, String> MOVIE_STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("0", "动作");
        TYPE_MAP.put("1", "喜剧");
        TYPE_MAP.put("2", "剧情");
        TYPE_MAP.put("3", "爱情");
        TYPE_MAP.put("4", "科幻");
        TYPE_MAP.put("5", "悬疑");
        TYPE_MAP.put("6", "恐怖");
        TYPE_MAP.put("7", "动画");
        TYPE_MAP.put("8", "战争");
        TYPE_MAP.put("9", "冒险");
        TYPE_MAP.put("10", "纪录片");
        TYPE_MAP.put("11", "奇幻");

        COUNTRY_MAP.put(0, "内地");
        COUNTRY_MAP.put(1, "欧美");
        COUNTRY_MAP.put(2, "日韩");

        LANGUAGE_MAP.put(0, "中文");
        LANGUAGE_MAP.put(1, "外语");

        MOVIE_STATUS_MAP.put(1, "正在热映");
        MOVIE_STATUS_MAP.put(0, "已下架");
        MOVIE_STATUS_MAP.put(2, "即将上映");

        ORDER_STATUS_MAP.put(0, "待支付");
        ORDER_STATUS_MAP.put(1, "已支付");
        ORDER_STATUS_MAP.put(2, "已取消");
        ORDER_STATUS_MAP.put(3, "已退款");
    }

    public static String typeCodesToNames(String typeCodes) {
        if (typeCodes == null || typeCodes.isBlank()) return "";
        return Arrays.stream(typeCodes.split(","))
                .map(String::trim)
                .map(code -> TYPE_MAP.getOrDefault(code, code))
                .reduce((a, b) -> a + "/" + b)
                .orElse(typeCodes);
    }

    public static String countryCodeToName(Integer code) {
        if (code == null) return "";
        return COUNTRY_MAP.getOrDefault(code, String.valueOf(code));
    }

    public static String languageCodeToName(Integer code) {
        if (code == null) return "";
        return LANGUAGE_MAP.getOrDefault(code, String.valueOf(code));
    }

    public static String movieStatusToName(Integer status) {
        if (status == null) return "";
        return MOVIE_STATUS_MAP.getOrDefault(status, String.valueOf(status));
    }

    public static String orderStatusToName(Integer status) {
        if (status == null) return "";
        return ORDER_STATUS_MAP.getOrDefault(status, String.valueOf(status));
    }
}