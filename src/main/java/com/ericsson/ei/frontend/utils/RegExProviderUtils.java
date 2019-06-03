package com.ericsson.ei.frontend.utils;

import java.io.IOException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.eiffelcommons.utils.RegExProvider;

public class RegExProviderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegExProviderUtils.class);

    public static String getRegEx(String key) {
        String regEx = "";
        try {
            regEx = RegExProvider.getRegExs().getString(key);
        } catch (JSONException | IOException e) {
            LOGGER.error("Error message: " + e.getMessage(), e);
        }
        return regEx;
    }
}
