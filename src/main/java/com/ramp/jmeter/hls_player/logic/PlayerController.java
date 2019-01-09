package com.ramp.jmeter.hls_player.logic;


import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.NextIsNullException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class PlayerController extends GenericController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlayerController.class);

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
        RequestInfo masterResponse = tryGetMasterList();


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
            int sz = nextSamplers.size();
            log.debug("nextSamplers size:" + sz);
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
                long sleepTime = lastSampler.getNextCallTimeMillis() - now;
                log.debug("PlayerController sleep time: " + (float) (sleepTime / 1000.0));
                Thread.sleep(sleepTime);
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
        if (lastSampler != null && lastSampler.getNextCallTimeMillis() != -1) {
            log.debug("adding lastSampler to priorityQueue");
            priorityQueue.add(lastSampler);
        } else {
            log.debug("ERROR: should not occur");
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

    //---------------------------Master Playlist Getting-----------------------------------//

    public static final String MASTER_PLAYLIST_URL = "MASTER_PLAYLIST_URL";
    public static final String IS_CUSTOM_DURATION = "IS_CUSTOM_DURATION";
    public static final String CUSTOM_DURATION = "CUSTOM_DURATION";

    private Parser parser;

    private RequestInfo tryGetMasterList() {
        try {
            SampleResult masterResult = new SampleResult();
            return getMasterList(masterResult, parser);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private RequestInfo getMasterList(SampleResult masterResult, Parser parser) throws IOException {

        masterResult.sampleStart();
        RequestInfo response = parser.getBaseUrl(new URL(getURLData()), masterResult, true);
        masterResult.sampleEnd();

        masterResult.setRequestHeaders(response.getRequestHeaders());
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

        masterResult.setHeadersSize(headerBytes);
        masterResult.setSentBytes(response.getSentBytes());
        masterResult.setDataEncoding(response.getContentEncoding());

        return response;

    }

    public String getURLData() {
        return this.getPropertyAsString(MASTER_PLAYLIST_URL);
    }


    public boolean isCustomDuration() {
        return this.getPropertyAsBoolean(IS_CUSTOM_DURATION);
    }

    public String getCustomDuration() {
        return this.getPropertyAsString(CUSTOM_DURATION);
    }
}
