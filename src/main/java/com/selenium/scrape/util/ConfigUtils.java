package com.selenium.scrape.util;

import org.apache.commons.configuration.Configuration;

/**
 * Created by huutuan on 03/03/2017.
 */
public class ConfigUtils {

    public static String getString(Configuration config, String key){
        String[] values = config.getStringArray(key);
        if (values.length == 1)
            return values[0];
        else {
            StringBuilder builder = new StringBuilder();
            for (String value : values) {
                builder.append(value).append(", ");
            }

            String rs = builder.toString();
            return rs.substring(0, rs.length() - 2);
        }
    }
}
