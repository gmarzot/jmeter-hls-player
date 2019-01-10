package com.ramp.jmeter.hls_player.logic;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.http.protocol.HTTP;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MediaPlaylistSampler extends AbstractSampler {
    private static final Logger log = LoggerFactory.getLogger(MediaPlaylistSampler.class);

    public static final String CUSTOM = "CUSTOM";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";

    public static final String MEDIA_PLAYLIST_TYPE = "HLS.MEDIA_PLAYLIST_TYPE";

    //Video
    public static final String RESOLUTION_OPTION = "HLS.RESOLUTION_OPTION";
    public static final String CUSTOM_RESOLUTION = "HLS.CUSTOM_RESOLUTION";
    public static final String BANDWIDTH_OPTION = "HLS.BANDWIDTH_OPTION";
    public static final String CUSTOM_BANDWIDTH = "HLS.CUSTOM_BANDWIDTH";
    //Audio
    public static final String CUSTOM_AUDIO = "HLS.CUSTOM_AUDIO";
    //Closed Captions
    public static final String CUSTOM_CC = "HLS.CUSTOM_CC";
    public static final String TYPE_VIDEO = "Video";
    public static final String TYPE_AUDIO = "Audio";
    public static final String TYPE_SUBTITLES = "Closed Captions";


    private Parser parser;

    private RequestInfo masterResponse = null;

    private String playlistUri = null;

    private boolean isFirstSample = true;
    private int targetDuration = 0; // HLS target duration

    private long lastTimeMillis = 0; // system millis - time of last transaction

    private long nextCallTime = 0;

    private final List<SegmentInfo> segmentsToGet = new ArrayList<>();
    private SegmentInfo lastExtracted = null;

    public MediaPlaylistSampler() {
        super();
        setName("HLS Media Playlist Sampler");
        parser = new Parser();
    }


    void setResolutionData(String res) {
        this.setProperty(CUSTOM_RESOLUTION, res);
    }

    private String getResolutionData() {
        return this.getPropertyAsString(CUSTOM_RESOLUTION);
    }

    void setBandwidthData(String net) {
        this.setProperty(CUSTOM_BANDWIDTH, net);
    }

    private String getBandwidthProp() {
        return this.getPropertyAsString(CUSTOM_BANDWIDTH);
    }

    void setResolutionType(String type) {
        this.setProperty(RESOLUTION_OPTION, type);
    }

    private String getResolutionType() {
        return this.getPropertyAsString(RESOLUTION_OPTION);
    }

    void setBandwidthType(String type) {
        this.setProperty(BANDWIDTH_OPTION, type);
    }

    private String getBandwidthType() {
        return this.getPropertyAsString(BANDWIDTH_OPTION);
    }

    private String getPlaylistUri(RequestInfo respond, Parser parser) throws MalformedURLException {
        URL masterURL = new URL(respond.getUrl());
        String basePath = masterURL.getPath().substring(0, masterURL.getPath().lastIndexOf('/') + 1);
        log.info("extracting " + this.getMediaPlaylistType() + " playlist uri from master playlist(" + respond.getUrl() + ") basePath(" + basePath + ")");
        String playlistUri = null;
        switch (this.getMediaPlaylistType()) {
            case "Video":
                playlistUri = parser.selectVideoPlaylist(respond.getResponse(),
                        this.getResolutionData(), this.getBandwidthProp(),
                        this.getBandwidthType(), this.getResolutionType());
                break;
            case "Audio":
                playlistUri = parser.selectAudioPlaylist(respond.getResponse(), this.getCustomAudio());
                break;
            case "Closed Captions":
                playlistUri = parser.selectSubtitlesPlaylist(respond.getResponse(), this.getCustomCC());
                break;
            default:
                log.error("Unexpected Media Playlist Type: " + this.getMediaPlaylistType());
                break;
        }

        if (playlistUri == null) {
            log.warn("Unable to select playlist, testing if media playlist supplied.");
            if (!respond.getResponse().contains("#EXTINF")) {
                log.error("The playlist is neither master or media");
                return null;
            }
            playlistUri = respond.getUrl(); // the supplied url is the media playlist possibly
        }

        String portString = masterURL.getPort() > 0 ? ":" + masterURL.getPort() : "";
        if (playlistUri.indexOf('/') == 0) {//relative to host
            playlistUri = masterURL.getProtocol() + "://" + masterURL.getHost() + portString + playlistUri;
        } else if (!playlistUri.startsWith("http")) {//relative to base path
            playlistUri = masterURL.getProtocol() + "://" + masterURL.getHost() + portString + basePath + playlistUri;
        }

        log.info("playlistUri: " + playlistUri);

        return playlistUri;
    }

    private String getCustomCC() {
        return this.getPropertyAsString(CUSTOM_CC);
    }

    private String getCustomAudio() {
        return this.getPropertyAsString(CUSTOM_AUDIO);
    }

    private String getMediaPlaylistType() {
        return this.getPropertyAsString(MEDIA_PLAYLIST_TYPE);
    }

    private RequestInfo getPlaylist(SampleResult playListResult, Parser parser) throws IOException {

        playListResult.sampleStart();
        RequestInfo response = parser.getBaseUrl(new URL(playlistUri), playListResult, true);
        playListResult.sampleEnd();

        playListResult.setRequestHeaders(response.getRequestHeaders());
        playListResult.setSuccessful(response.isSuccess());
        playListResult.setResponseMessage(response.getResponseMessage());
        playListResult.setSampleLabel("playlist");
        playListResult.setResponseHeaders(response.getHeadersAsString());
        playListResult.setResponseData(response.getResponse().getBytes());
        playListResult.setResponseCode(response.getResponseCode());
        playListResult.setContentType(response.getContentType());
        playListResult.setBytes(playListResult.getBytesAsLong() + (long) playListResult.getRequestHeaders().length());

        int headerBytes = playListResult.getResponseHeaders().length() // condensed
                // length
                // (without
                // \r)
                + response.getHeaders().size() // Add \r for each header
                + 1 // Add \r for initial header
                + 2; // final \r\n before data

        playListResult.setHeadersSize(headerBytes);
        playListResult.setSentBytes(response.getSentBytes());
        playListResult.setDataEncoding(response.getContentEncoding());

        return response;
    }

    @Override
    public SampleResult sample(Entry e) {
        try {
            if (masterResponse == null) {
                log.warn("Master Playlist Missing");
                nextCallTime = -1;
                return null;
            }
            if (playlistUri == null) {
                playlistUri = getPlaylistUri(masterResponse, parser);
                if (playlistUri == null) {
                    log.error("Unable to get playlist path");
                    nextCallTime = -1;
                    return null;
                }
            }

            log.debug("sample: playlistUri: " + playlistUri + " thread: " + Thread.currentThread().getName());

            if (segmentsToGet.isEmpty()) {
                long now = System.currentTimeMillis();
                long nextPlaylistGetTime = lastTimeMillis + (targetDuration * 500);
                if ((targetDuration > 0) && now < nextPlaylistGetTime) {
                    try {
                        Thread.sleep(now - nextPlaylistGetTime); // only get playlist every TD/2 seconds
                    } catch (InterruptedException e1) {
                        log.warn("sample: Thread.sleep() interrupted");
                    }
                }
                SampleResult playlistResult = new SampleResult();
                RequestInfo playlistResponse = getPlaylist(playlistResult, parser);

                // does not have an end tag
                boolean isLive = parser.isLive(playlistResponse.getResponse());
                if (isLive) {
                    log.debug("sample: detected live playlist");
                    if (isFirstSample) {
                        log.debug("sample: first live playlist: adding 3 segments");
                        segmentsToGet.addAll(parser.extractSegmentUris(playlistResponse.getResponse(), 3));
                        isFirstSample = false;
                    } else {
                        log.debug("sample: live playlist: lastExtracted: " + lastExtracted);
                        segmentsToGet.addAll(parser.extractSegmentUris(playlistResponse.getResponse(), lastExtracted));
                    }
                } else {
                    log.debug("Not live");
                    segmentsToGet.addAll(parser.extractSegmentUris(playlistResponse.getResponse(), lastExtracted));
                }

                log.debug("parsed " + segmentsToGet.size() + " segmentsToGet out of playlist");
                lastTimeMillis = now;

                int td = parser.getTargetDuration(playlistResponse.getResponse());
                if (td < 0) {
                    log.error("sample: playlist contains no target duration: \\n" + playlistResponse.getResponse());
                } else if (targetDuration != 0 && td != targetDuration) {
                    log.info("sample: playlist target duration changed: " + td + " (" + targetDuration + ")");
                }
                if (td > 0) {
                    targetDuration = td;
                }

                nextCallTime = System.currentTimeMillis();
                return playlistResult;
            } else {
                SegmentInfo segment = segmentsToGet.remove(0);
                String durationStr = segment.getDuration();
                float duration = Float.parseFloat(durationStr);
                log.debug("segment duration: " + durationStr + " (" + duration + ")");
                String segmentBaseUri = playlistUri.substring(0, playlistUri.lastIndexOf('/') + 1);
                SampleResult segmentResult = getSegment(parser, segment, segmentBaseUri);
                lastExtracted = segment;
                nextCallTime = System.currentTimeMillis() + ((long) (duration * 1000)) - segmentResult.getTime();
                log.debug("Next Call Time: " + nextCallTime);
                return segmentResult;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        log.warn("Sampler returning null!");
        nextCallTime = System.currentTimeMillis();
        return null;
    }

    private HTTPSampleResult failSample(HTTPSampleResult sampleResult){
        sampleResult.setSuccessful(false);
        return sampleResult;
    }

    private SampleResult getSegment(Parser parser, SegmentInfo segmentInfo, String url) {
        HTTPSampleResult sampleResult = new HTTPSampleResult();

        String uriString = segmentInfo.getUri();
        // log.info("url: " + (url != null ? url : "<null>") + " uriString: " + uriString);
        if ((url != null) && (!uriString.startsWith("http"))) {
            uriString = url + uriString;
        }
        log.debug("Calculated segment URI: %s, thread: %s", uriString, getThreadName());

        URL segmentURL;
        HttpURLConnection connection;

        try{
            // Create URL object
            segmentURL = new URL(uriString);
        } catch (MalformedURLException e){
            // uriString is not a valid URL
            e.printStackTrace();
            return failSample(sampleResult);
        }

        try {
            // Create the connection object
            connection = (HttpURLConnection) segmentURL.openConnection();
            connection.setRequestMethod(HTTPConstants.GET);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
            return failSample(sampleResult);
        }

        // Get the request Headers (there are no headers)
        //String requestHeaderString = parser.HeadersToString(connection.getRequestProperties());

        // Start recording sample
        sampleResult.sampleStart();

        try{
            // manually open the connection
            connection.connect();
            sampleResult.connectEnd();
            connection.getContent();
        } catch (IOException e) {
            // socket error
            e.printStackTrace();
            sampleResult.sampleEnd();
            return failSample(sampleResult);
        }

        long responseSize = -1;

        try{
            // Read in the segment data
            CountingInputStream in = new CountingInputStream(connection.getInputStream());
            sampleResult.latencyEnd();
            in.readAllBytes();
            responseSize = in.getByteCount();
            in.close();
        } catch (IOException e) {
            // Input stream error
            e.printStackTrace();
            sampleResult.sampleEnd();
            return failSample(sampleResult);
        }

        // Record the end time of a sample and calculate the elapsed time
        sampleResult.sampleEnd();

        try { // Save data to sample result object
            // Set Sample label
            switch (this.getMediaPlaylistType()) {
                case TYPE_VIDEO:
                    sampleResult.setSampleLabel("video_segment");
                    break;
                case TYPE_AUDIO:
                    sampleResult.setSampleLabel("audio_segment");
                    break;
                case TYPE_SUBTITLES:
                    sampleResult.setSampleLabel("subtitle_segment");
                    break;
                default:
                    log.error("Unknown media type");
            }

            // Set Request Data
            sampleResult.setURL(segmentURL);
            sampleResult.setHTTPMethod(connection.getRequestMethod());

            // Set Response Data
            sampleResult.setResponseCode(Integer.toString(connection.getResponseCode()));
            String responseHeaderString = parser.HeadersToString(connection.getHeaderFields());
            sampleResult.setResponseHeaders(responseHeaderString);
            sampleResult.setHeadersSize(responseHeaderString.getBytes().length);
            sampleResult.setDataEncoding(connection.getContentEncoding());
            sampleResult.setBodySize(responseSize);
            sampleResult.setResponseMessage(connection.getResponseMessage());
            sampleResult.setContentType(connection.getContentType());

            // Set SampleResult Metadata
            sampleResult.setSuccessful(connection.getResponseCode() >= 200
                    && connection.getResponseCode() < 400);

        } catch (IOException e1) {
            e1.printStackTrace();
            log.error("unexpected error processing response data");
        }

        return sampleResult;
    }

    void setParser(Parser p) {
        parser = p;
    }

    long getNextCallTimeMillis() {
        return nextCallTime;
    }

    void setMasterPlaylist(RequestInfo masterResponse) {
        this.masterResponse = masterResponse;
    }
}
