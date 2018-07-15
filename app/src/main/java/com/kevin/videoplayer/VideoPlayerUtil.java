package com.kevin.videoplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin Jern on 2018/7/15 14:22.
 */
public class VideoPlayerUtil {
    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(int milliseconds) {
        if (milliseconds < 0 || milliseconds > 24 * 60 * 60 * 1000) {
            return "00:00";
        }
//        // 总共秒数
//        int totalSeconds = milliseconds / 1000;
//        // 秒数
//        int seconds = totalSeconds % 60;
//        // 分钟数
//        int minutes = (totalSeconds / 60) % 60;
//        // 小时数
//        int hours = totalSeconds / 3600;
//        StringBuilder builder = new StringBuilder();
//        Formatter formatter = new Formatter(builder, Locale.getDefault());
//        if (hours > 0) {
//            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//        } else {
//            return formatter.format("%02d:%02d", minutes, seconds).toString();
//        }

        Date date = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date).toString();
    }
}
