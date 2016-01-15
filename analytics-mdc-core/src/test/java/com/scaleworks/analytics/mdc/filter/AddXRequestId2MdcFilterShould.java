package com.scaleworks.analytics.mdc.filter;

import lombok.Getter;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.scaleworks.analytics.mdc.filter.AddXRequestId2MdcFilter.X_REQUEST_ID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by Yugang.Zhou on 1/7/16.
 */
public class AddXRequestId2MdcFilterShould {

    private AddXRequestId2MdcFilterStub subject = new AddXRequestId2MdcFilterStub();

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockFilterChain filterChain = new MockFilterChain();

    @Before
    public void clearMdc() {
        MDC.clear();
    }

    @Test
    public void addXRequestIdFromHttpHeaderToMDC_whenRequestInComing() throws IOException, ServletException {
        requestIsNotInMdc();

        String reqId = "a unique string";

        request.addHeader(X_REQUEST_ID, reqId);

        subject.doFilter(request, response, filterChain);

        requestIdWasAddedToMdcWithValue(equalTo(reqId));
        requestIdHasBeenClearedFromMdcAfterFilterCompletion();

        assertThat(response.getHeader(X_REQUEST_ID), is(reqId));
    }


    @Test
    public void generateXRequestIdToMDC_whenRequestInComing_givenXRequestIdIsAbsentInHttpHeader() throws IOException, ServletException {
        requestIsNotInMdc();

        subject.doFilter(request, response, filterChain);

        requestIdWasAddedToMdcWithValue(randomUuid());
        requestIdHasBeenClearedFromMdcAfterFilterCompletion();
        assertThat(response.getHeader(X_REQUEST_ID), is(randomUuid()));
    }

    private Matcher<String> randomUuid() {
        return not(nullValue(String.class));
    }

    private void requestIdWasAddedToMdcWithValue(Matcher<String> matcher) {
        assertThat(subject.getValuePut(), matcher);
    }

    private void requestIdHasBeenClearedFromMdcAfterFilterCompletion() {
        requestIsNotInMdc();
    }

    private void requestIsNotInMdc() {
        assertThat(MDC.get(X_REQUEST_ID), is(nullValue()));
    }

    static class AddXRequestId2MdcFilterStub extends AddXRequestId2MdcFilter {
        @Getter
        private String valuePut;

        @Override
        protected void postMdcPut() {
            this.valuePut = MDC.get(X_REQUEST_ID);
        }
    }

}