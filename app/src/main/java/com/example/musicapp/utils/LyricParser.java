package com.example.musicapp.utils;

import com.example.musicapp.model.LyricLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LyricParser - LRC 歌词解析工具
 */
public class LyricParser {

    private static final Pattern TIME_PATTERN =
            Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:\\.(\\d{1,3}))?\\]");

    public static List<LyricLine> parse(String lrcText) {
        List<LyricLine> result = new ArrayList<>();
        // 空内容直接返回空列表，避免 NPE
        if (lrcText == null || lrcText.isEmpty()) {
            return result;
        }

        // LRC 用 \n 分行（API 返回的就是 \n 转义字符串）
        String[] lines = lrcText.split("\\n");
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            // 找出本行所有的时间标签
            Matcher matcher = TIME_PATTERN.matcher(line);
            List<Long> times = new ArrayList<>();
            int lastTagEnd = 0;
            while (matcher.find()) {
                long timeMs = parseTime(matcher);
                times.add(timeMs);
                lastTagEnd = matcher.end();
            }

            if (times.isEmpty()) {
                continue;
            }

            // 时间标签之后的部分就是歌词内容
            String content = line.substring(lastTagEnd).trim();
            // 内容为空的也跳过（避免显示空白行影响体验）
            if (content.isEmpty()) {
                continue;
            }

            // 把每个时间标签都生成一行歌词
            for (Long t : times) {
                result.add(new LyricLine(t, content));
            }
        }

        // 按时间升序排序，方便后续二分查找
        Collections.sort(result, new Comparator<LyricLine>() {
            @Override
            public int compare(LyricLine o1, LyricLine o2) {
                return Long.compare(o1.timeMs, o2.timeMs);
            }
        });
        return result;
    }

    /**
     * 把正则匹配到的 [mm:ss.xx] 转换成毫秒
     */
    private static long parseTime(Matcher matcher) {
        int minute = Integer.parseInt(matcher.group(1));
        int second = Integer.parseInt(matcher.group(2));
        String msStr = matcher.group(3);
        int millis = 0;
        if (msStr != null && !msStr.isEmpty()) {
            if (msStr.length() == 2) {
                millis = Integer.parseInt(msStr) * 10;
            } else if (msStr.length() == 1) {
                millis = Integer.parseInt(msStr) * 100;
            } else {
                millis = Integer.parseInt(msStr);
            }
        }
        return (minute * 60L + second) * 1000L + millis;
    }

    public static int findCurrentLineIndex(List<LyricLine> lines, long currentMs) {
        if (lines == null || lines.isEmpty()) {
            return -1;
        }
        if (currentMs < lines.get(0).timeMs) {
            return -1;
        }

        int low = 0;
        int high = lines.size() - 1;
        while (low < high) {
            int mid = (low + high + 1) / 2;
            if (lines.get(mid).timeMs <= currentMs) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }
}
