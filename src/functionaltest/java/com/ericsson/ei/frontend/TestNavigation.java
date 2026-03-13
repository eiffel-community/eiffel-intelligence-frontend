package com.ericsson.ei.frontend;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ericsson.ei.frontend.pageobjects.IndexPage;

public class TestNavigation extends SeleniumBaseClass {

    @MockitoBean
    protected CloseableHttpClient mockedHttpClient;

    private IndexPage indexPage;

    @Before
    public void before() {
        indexPage = new IndexPage(mockedHttpClient, driver, baseUrl);
        indexPage.loadPage();
    }

    @Test
    public void testPageNavigationButtons() throws IOException {
        indexPage.clickTestRulesPage();
        indexPage.clickEiInfoBtn();
        indexPage.clickInformationBtn();
        indexPage.clickRulesBtn();
        indexPage.clickSwitchBackendInstanceBtn();
        indexPage.clickSubscriptionPage();
    }
}
