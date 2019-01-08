package com.ramp.jmeter.hls_player.logic;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class RequestInfoTest {

    @BeforeClass
    public static void setUpClass()
            throws Exception {
    }

    @Test
    public void testHeaders() {

        RequestInfo dt = new RequestInfo();
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> header1 = new ArrayList<String>();
        List<String> header2 = new ArrayList<String>();
        List<String> header3 = new ArrayList<String>();

        header1.add("header11");
        header1.add("header12");
        header1.add("header13");

        header2.add("header21");
        header2.add("header22");
        header2.add("header23");

        header3.add("header31");

        headers.put("headerKey1", header1);
        headers.put("headerKey2", header2);
        headers.put("headerKey3", header3);

        dt.setHeaders(headers);

        assertEquals("headerKey1 : header11 header12 header13\nheaderKey2 : header21 header22 header23\nheaderKey3 : header31\n",
                dt.getHeadersAsString());

    }
}
