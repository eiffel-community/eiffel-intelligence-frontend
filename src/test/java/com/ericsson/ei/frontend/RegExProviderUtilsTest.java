package com.ericsson.ei.frontend;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.ericsson.ei.frontend.utils.RegExProviderUtils;

public class RegExProviderUtilsTest {

    @Test
    public void test() {
        String regExForInvalidName = RegExProviderUtils.getRegEx("invalidName");
        String regExForValidEmail = RegExProviderUtils.getRegEx("validEmail");
        assertFalse(regExForInvalidName.isEmpty());
        assertFalse(regExForInvalidName.isEmpty());
    }
}
