package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.Float;
import java.lang.Thread;

public class MediaPlaylistSampler extends AbstractSampler {
    public static final String HEADER_MANAGER = "HLSRequest.header_manager"; // $NON-NLS-1$
    public static final String COOKIE_MANAGER = "HLSRequest.cookie_manager"; // $NON-NLS-1$
    public static final String CACHE_MANAGER = "HLSRequest.cache_manager"; // $NON-NLS-1$
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


    private DataRequest masterResponse = null;

    private String playlistUri = null;

    private DataRequest playlistResponse = null;
    private SampleResult playlistResult = null;

    private boolean isLive = false; // does not have an end tag
    private boolean isFirstSample = true;
    private int targetDuration = 0; // HLS target duration

    private long lastTimeMillis = 0; // system millis - time of last transaction

    private long nextCallTime = 0;

    private List<SegmentInfo> segmentsToGet = new ArrayList<>();
    private SegmentInfo lastExtracted = null;

    public MediaPlaylistSampler() {
        super();
        setName("HLS Media Playlist Sampler");
        parser = new Parser();
    }

    public HeaderManager getHeaderManager() {
        return (HeaderManager) getProperty(MediaPlaylistSampler.HEADER_MANAGER).getObjectValue();
    }

    void setPlaylistUri(DataRequest request) {
        try {
            playlistUri = getPlaylistUri(request, parser);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    private String getPlaylistUri(DataRequest respond, Parser parser) throws MalformedURLException {
        URL masterURL = new URL(respond.url);
        String basePath = masterURL.getPath().substring(0, masterURL.getPath().lastIndexOf('/') + 1);
        log.info("extracting "+this.getMediaPlaylistType()+" playlist uri from master playlist("+respond.url+") baseurl("+basePath+")");
        String playlistUri = null;
        switch (this.getMediaPlaylistType()) {
            case "Video":
                playlistUri = parser.selectVideoPlaylist(respond.getResponse(),
                        this.getRESDATA(), this.getNetwordData(),
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
            if(!respond.getResponse().contains("#EXTINF")){
                log.error("The playlist is neither master or media");
                return null;
            }
            playlistUri = respond.url; // the supplied url is the media playlist possibly
        }

        if (playlistUri.indexOf('/') == 0) {//relative to host
            playlistUri = masterURL.getProtocol() + "://" + masterURL.getHost() + (masterURL.getPort() > 0 ? ":" + masterURL.getPort() : "") + playlistUri;
        } else if (!playlistUri.startsWith("http")) {//relative to base path
            playlistUri = masterURL.getProtocol() + "://" + masterURL.getHost() + (masterURL.getPort() > 0 ? ":" + masterURL.getPort() : "") + basePath + playlistUri;
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

    private DataRequest getPlaylist(SampleResult playListResult, Parser parser) throws IOException {

        playListResult.sampleStart();
        DataRequest response = parser.getBaseUrl(new URL(playlistUri), playListResult, true);
        playListResult.sampleEnd();

        playListResult.setRequestHeaders(response.getRequestHeaders() + "\n\n" + getCookieHeader(playlistUri) + "\n\n"
                + getRequestHeader(this.getHeaderManager()));
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

        playListResult.setHeadersSize((int) headerBytes);
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
		if ((targetDuration > 0) && now < (lastTimeMillis + (targetDuration * 500))) {
		    try {
			Thread.sleep(now - (targetDuration * 500)); // only get playlist every TD/2 seconds
		    } catch (InterruptedException e1) {
			log.warn("sample: Thead.sleep() interrupted");
		    }
		}
		playlistResult = new SampleResult();
		playlistResponse = getPlaylist(playlistResult, parser);

		isLive = parser.isLive(playlistResponse.getResponse());
		if (isLive) {
		    log.info("sample: detected live playlist");
		    if (isFirstSample) {
			log.debug("First Sample");
			segmentsToGet.addAll(parser.extractSegmentUris(playlistResponse.getResponse(), 3));
			isFirstSample = false;
		    }else {
			log.debug("Not first sample");
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
        nextCallTime = -1;
        return null;
    }

    public String getRESDATA() {
        return this.getPropertyAsString(CUSTOM_RESOLUTION);
    }

    public String getNetwordData() {
        return this.getPropertyAsString(CUSTOM_BANDWIDTH);
    }

    public String getResolutionType() {
        return this.getPropertyAsString(RESOLUTION_OPTION);
    }

    public String getBandwidthType() {
        return this.getPropertyAsString(BANDWIDTH_OPTION);
    }


    public void setResData(String res) {
        this.setProperty(CUSTOM_RESOLUTION, res);
    }

    public void setNetworkData(String net) {
        this.setProperty(CUSTOM_BANDWIDTH, net);
    }

    public void setResolutionType(String type) {
        this.setProperty(RESOLUTION_OPTION, type);
    }

    public void setBandwidthType(String type) {
        this.setProperty(BANDWIDTH_OPTION, type);
    }

    public SampleResult getSegment(Parser parser, SegmentInfo segmentInfo, String url) {
        HTTPSampleResult sampleResult = new HTTPSampleResult();

        String uriString = segmentInfo.getUri();
        // log.info("url: " + (url != null ? url : "<null>") + " uriString: " + uriString);
        if ((url != null) && (!uriString.startsWith("http"))) {
            uriString = url + uriString;
        }

        sampleResult.sampleStart();

        try {
            // Fetch Segment
            //DataRequest respond = parser.getBaseUrl(new URL(uriString), result, false);

            // Set the URL
            URL httpURL = new URL(uriString);

            // Create the connection object
            HttpURLConnection connection = (HttpURLConnection) httpURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            // Get the request Headers
            String requestHeaderString = parser.HeadersToString(connection.getRequestProperties());

            // manually open and close the connection
            connection.connect();
            sampleResult.connectEnd();

            InputStream in = connection.getInputStream();
            byte[] responseData = in.readAllBytes();
            in.close();

            connection.disconnect();

            // Record the end time of a sample and calculate the elapsed time
            sampleResult.sampleEnd();

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
            sampleResult.setRequestHeaders(requestHeaderString);
            sampleResult.setSentBytes(requestHeaderString.getBytes().length);

            // Set Response Data
            sampleResult.setResponseCode(Integer.toString(connection.getResponseCode()));
            String responseHeaderString = parser.HeadersToString(connection.getHeaderFields());
            sampleResult.setResponseHeaders(responseHeaderString);
            sampleResult.setDataEncoding(connection.getContentEncoding());
            sampleResult.setResponseData(responseData);
            sampleResult.setResponseMessage(connection.getResponseMessage());
            sampleResult.setContentType(connection.getContentType());
            sampleResult.setHeadersSize(responseHeaderString.getBytes().length);
            // TODO: verify this calculation for total bytes received (complete http response including headers)
            sampleResult.setBytes(connection.getContentLengthLong() + sampleResult.getHeadersSize());

            // Set SampleResult Metadata
            sampleResult.setSuccessful(connection.getResponseCode() >= 200
                    && connection.getResponseCode() < 300);

        } catch (IOException e1) {
            e1.printStackTrace();
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(false);
            sampleResult.setResponseMessage("Exception: " + e1);
        }

        return sampleResult;
    }

    // private method to allow AsyncSample to reset the value without performing
    // checks
    private void setCookieManagerProperty(CookieManager value) {
        setProperty(new TestElementProperty(COOKIE_MANAGER, value));
    }

    public void setCookieManager(CookieManager value) {
        CookieManager mgr = getCookieManager();
        if (mgr != null) {
            log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
        }
        setCookieManagerProperty(value);
    }

    public CookieManager getCookieManager() {
        return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
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
        StringBuilder headerStringb = new StringBuilder();

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
                        headerStringb.append(n).append(": ").append(v).append("\n");
                    }
                }
            }
        }

        return headerStringb.toString();
    }

    public void setHeaderManager(HeaderManager value) {
        HeaderManager mgr = getHeaderManager();
        if (mgr != null) {
            value = mgr.merge(value);
            if (log.isDebugEnabled()) {
                log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                for (int i = 0; i < value.getHeaders().size(); i++) {
                    log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                }
            }
        }
        setProperty(new TestElementProperty(HEADER_MANAGER, (TestElement) value));
    }

    public String getCookieHeader(String urlData) throws MalformedURLException {
        String headerString = "";

        URL url = new URL(urlData);
        // Extracts all the required cookies for that particular URL request
        String cookieHeader = null;
        if (getCookieManager() != null) {
            cookieHeader = getCookieManager().getCookieHeaderForURL(url);
            if (cookieHeader != null) {
                headerString = headerString + HTTPConstants.HEADER_COOKIE + ": " + cookieHeader + "\n";
            }
        }

        return headerString;
    }

    @Override
    public void addTestElement(TestElement el) {
        if (el instanceof HeaderManager) {
            setHeaderManager((HeaderManager) el);
        } else if (el instanceof CookieManager) {
            setCookieManager((CookieManager) el);
        } else if (el instanceof CacheManager) {
            setCacheManager((CacheManager) el);
        } else {
            super.addTestElement(el);
        }
    }

    public void setParser(Parser p) {
        parser = p;
    }

    public long getNextCallTimeMillis() {
        return nextCallTime;
    }

    public void setMasterPlaylist(DataRequest masterResponse) {
        this.masterResponse = masterResponse;
    }
}
