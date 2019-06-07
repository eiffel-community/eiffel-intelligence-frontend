package com.ericsson.ei.frontend;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ericsson.ei.frontend.utils.RegExProviderUtils;

public class RegExProviderUtilsTest {

    @Test
    public void test() {
        String regExForInvalidName = RegExProviderUtils.getRegEx("invalidName");
        String regExForValidEmail = RegExProviderUtils.getRegEx("validEmail");
        assertEquals(regExForInvalidName, "(\\W)");
        assertEquals(regExForValidEmail,
                "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    }
}
