package com.ramp.jmeter.hls_player.logic;


import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.NextIsNullException;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class PlayerController extends GenericController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerController.class);

    public static final String CUSTOM = "CUSTOM";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";

    //Test Duration
    private long startTime;
    private long duration;

    //Last Sampler
    private MediaPlaylistSampler lastSampler;

    //Priority Queue
    private PriorityQueue<MediaPlaylistSampler> priorityQueue;
    private Queue<MediaPlaylistSampler> nextSamplers;

    public PlayerController() {
        super();
        setName("HLS Player");


    }



    @Override
    public void initialize() {
        log.debug("initialize");
        parser = new Parser();
        DataRequest masterResponse = tryGetMasterList();


        priorityQueue = new PriorityQueue<>(new MediaPlaylistSamplerComparator());
        nextSamplers = new LinkedList<>();

        for (TestElement te : subControllersAndSamplers) {
            if (te instanceof MediaPlaylistSampler) {
                MediaPlaylistSampler mediaPlaylistSampler = (MediaPlaylistSampler) te;
                mediaPlaylistSampler.setMasterPlaylist(masterResponse);
                this.nextSamplers.add(mediaPlaylistSampler);
            }
        }
        startTime = -1;

        if (this.getPropertyAsBoolean(IS_CUSTOM_DURATION)) {
            duration = this.getPropertyAsLong(CUSTOM_DURATION);
        } else {
            duration = -1;
        }
        super.initialize();
    }

    protected TestElement getCurrentElement() throws NextIsNullException {
        if (nextSamplers.size() > 0) {
            lastSampler = nextSamplers.remove();
            return lastSampler;
        }

        if (priorityQueue.size() <= 0) {
            log.error("Priority Queue is empty");
            throw new NextIsNullException();
        }

        lastSampler = priorityQueue.remove();

        long now = System.currentTimeMillis();
        while (priorityQueue.size() > 0
                && (priorityQueue.comparator().compare(lastSampler, priorityQueue.peek()) == 0
                || priorityQueue.peek().getNextCallTimeMillis() < now)
                ) {
            nextSamplers.add(priorityQueue.remove());
        }
        if (lastSampler.getNextCallTimeMillis() > now) {
            try {
                Thread.sleep(lastSampler.getNextCallTimeMillis() - now);
            } catch (InterruptedException exception) {
                log.warn("Player sleep interrupted");
                this.setDone(true);
                return null;
            }
        }
        return lastSampler;
    }

    @Override
    public Sampler next() {
        log.debug("size of subControllersAndSamplers " + subControllersAndSamplers.size());
        log.debug("duration: " + duration);
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
        if (duration != -1) {
            long timeElapsed = System.currentTimeMillis() - startTime;
            if (timeElapsed > duration * 1000) {
                this.setDone(true);
                log.debug("out of time!");
                return null;
            }
        }
        if (lastSampler != null) {
            priorityQueue.add(lastSampler);
        }
        Sampler returnValue = super.next();
        if (returnValue == null) log.error("sampler was null");
        return returnValue;
    }

    @Override
    protected Sampler nextIsNull() throws NextIsNullException {
        log.debug("nextIsNull");
        return super.nextIsNull();
    }

    //Master Playlist Getting

    public static final String HEADER_MANAGER = "HLSRequest.header_manager"; // $NON-NLS-1$
    public static final String COOKIE_MANAGER = "HLSRequest.cookie_manager"; // $NON-NLS-1$
    public static final String CACHE_MANAGER = "HLSRequest.cache_manager"; // $NON-NLS-1$

    public static final String MASTER_PLAYLIST_URL = "MASTER_PLAYLIST_URL";
    public static final String IS_CUSTOM_DURATION = "IS_CUSTOM_DURATION";
    public static final String CUSTOM_DURATION = "CUSTOM_DURATION";

    private Parser parser;

    public DataRequest tryGetMasterList() {
        try {
            SampleResult masterResult = new SampleResult();
            return getMasterList(masterResult, parser);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private DataRequest getMasterList(SampleResult masterResult, Parser parser) throws IOException {

        masterResult.sampleStart();
        DataRequest response = parser.getBaseUrl(new URL(getURLData()), masterResult, true);
        masterResult.sampleEnd();

        masterResult.setRequestHeaders(response.getRequestHeaders() + "\n\n" + getCookieHeader(getURLData()) + "\n\n"
                + getRequestHeader(this.getHeaderManager()));
        masterResult.setSuccessful(response.isSuccess());
        masterResult.setResponseMessage(response.getResponseMessage());
        masterResult.setSampleLabel("master");
        masterResult.setResponseHeaders(response.getHeadersAsString());
        masterResult.setResponseData(response.getResponse().getBytes());
        masterResult.setResponseCode(response.getResponseCode());
        masterResult.setContentType(response.getContentType());
        masterResult.setBytes(masterResult.getBytesAsLong() + (long) masterResult.getRequestHeaders().length());

        int headerBytes = masterResult.getResponseHeaders().length() // condensed
                // length
                // (without
                // \r)
                + response.getHeaders().size() // Add \r for each header
                + 1 // Add \r for initial header
                + 2; // final \r\n before data

        masterResult.setHeadersSize((int) headerBytes);
        masterResult.setSentBytes(response.getSentBytes());
        masterResult.setDataEncoding(response.getContentEncoding());

        return response;

    }

    public void setCacheManager(CacheManager value) {
        CacheManager mgr = getCacheManager();
        if (mgr != null) {
            log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
        }
        setCacheManagerProperty(value);
    }

    // private method to allow AsyncSample to reset the value without performing
    // checks
    private void setCacheManagerProperty(CacheManager value) {
        setProperty(new TestElementProperty(CACHE_MANAGER, value));
    }
    public CacheManager getCacheManager() {
        return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
    }

    public String getRequestHeader(org.apache.jmeter.protocol.http.control.HeaderManager headerManager) {
        String headerString = "";

        if (headerManager != null) {
            CollectionProperty headers = headerManager.getHeaders();
            if (headers != null) {
                for (JMeterProperty jMeterProperty : headers) {
                    org.apache.jmeter.protocol.http.control.Header header = (org.apache.jmeter.protocol.http.control.Header) jMeterProperty
                            .getObjectValue();
                    String n = header.getName();
                    if (!HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)) {
                        String v = header.getValue();
                        v = v.replaceFirst(":\\d+$", "");
                        headerString = headerString + n + ": " + v + "\n";
                    }
                }
            }
        }

        return headerString;
    }

    public void setHeaderManager(HeaderManager value) {
        HeaderManager mgr = getHeaderManager();
        if (mgr != null) {
            value = mgr.merge(value, true);
            if (log.isDebugEnabled()) {
                log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                for (int i = 0; i < value.getHeaders().size(); i++) {
                    log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                }
            }
        }
        setProperty(new TestElementProperty(HEADER_MANAGER, (TestElement) value));
    }
    public HeaderManager getHeaderManager() {
        return (HeaderManager) getProperty(MediaPlaylistSampler.HEADER_MANAGER).getObjectValue();
    }

    public void setCookieManager(CookieManager value) {
        CookieManager mgr = getCookieManager();
        if (mgr != null) {
            log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
        }
        setCookieManagerProperty(value);
    }
    // private method to allow AsyncSample to reset the value without performing
    // checks
    private void setCookieManagerProperty(CookieManager value) {
        setProperty(new TestElementProperty(COOKIE_MANAGER, value));
    }

    public CookieManager getCookieManager() {
        return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
    }
    public String getCookieHeader(String urlData) throws MalformedURLException {
        String headerString = "";

        URL url = new URL(urlData);
        // Extracts all the required cookies for that particular URL request
        if (getCookieManager() != null) {
            String cookieHeader = getCookieManager().getCookieHeaderForURL(url);
            if (cookieHeader != null) {
                headerString = headerString + HTTPConstants.HEADER_COOKIE + ": " + cookieHeader + "\n";
            }
        }

        return headerString;
    }


    private String getURLData() {
        return this.getPropertyAsString(MASTER_PLAYLIST_URL);
    }



}
