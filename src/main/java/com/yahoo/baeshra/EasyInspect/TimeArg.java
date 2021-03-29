package com.yahoo.baeshra.EasyInspect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeArg {
    
    private static final Pattern REGEX_PATTERN = Pattern.compile("(\\d+)([hdms]{0,1})");

    public enum TimeUnit {
        SECOND(1, "Second"),
        MINUTE(60, "Minute"),
        HOUR(3600, "Hour"),
        DAY(86400, "Day");

        private final int multiplier;
        private final String description;

        private TimeUnit(int multiplier, String description) {
            this.multiplier = multiplier;
            this.description = description;
        }

        public int GetSecondMultiplier() {
            return multiplier;
        }

        public String GetDescription() {
            return description;
        }
    }

    private TimeUnit unit;
    private int seconds;

    public TimeUnit getTimeUnit() {
        return unit;
    }

    public int getSeconds() {
        return seconds;
    }

    public TimeArg(String input, TimeUnit defaultUnit, int defaultAmount) {

        TimeUnit unit = defaultUnit;
        int numberOfUnits = defaultAmount;

        if(input == null) {
            this.unit = defaultUnit;
            this.seconds = defaultUnit.GetSecondMultiplier() * defaultAmount;
            return;
        }
        Matcher matcher = TimeArg.REGEX_PATTERN.matcher(input.toLowerCase());
        if(matcher.find()) {
            numberOfUnits = Integer.parseInt(matcher.group(1));
            if(matcher.group(2) != null) {
                switch(matcher.group(2)) {
                    case "s": {
                        unit = TimeUnit.SECOND;
                        break;
                    }
                    case "m": {
                        unit = TimeUnit.MINUTE;
                        break;
                    }
                    case "h": {
                        unit = TimeUnit.HOUR;
                        break;
                    }
                    case "d": {
                        unit = TimeUnit.DAY;
                        break;
                    }
                }
            }
            this.unit = unit;
            this.seconds = numberOfUnits * unit.GetSecondMultiplier();
            return;
        }
    }

}
