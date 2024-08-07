package net.univwork.api.api_v1.tool;

import java.sql.Timestamp;

public class TimestampTool {

    public static boolean calculateHowMillisecondsDiffEndToStart(Timestamp start, Timestamp end, long milliseconds) {
        long millisecondsDifference = Math.abs(end.getTime() - start.getTime());
        return millisecondsDifference >= milliseconds;
    }
}
