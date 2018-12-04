package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.samplers.SampleResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class Parser implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Parser.class);

    public Parser() {
    }

    // HTTP GET request
    public DataRequest getBaseUrl(URL url, SampleResult sampleResult, boolean setRequest) throws IOException {
        HttpURLConnection con = null;
        DataRequest result = new DataRequest();
        boolean first = true;
        long sentBytes = 0;

        con = (HttpURLConnection) url.openConnection();

        sampleResult.connectEnd();

        result.url = url.toString();

        // By default it is GET request
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        // Set request header
        result.setRequestHeaders(con.getRequestMethod() + "  " + url.toString() + "\n");

	int responseCode = con.getResponseCode();

        // Reading response from input Stream
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {

            if (setRequest)
                response.append(inputLine + "\n");

            sentBytes += inputLine.getBytes().length + 1;

            if (first) {
                sampleResult.latencyEnd();
                first = false;
            }
        }

        in.close();

        // Set response parameters
        result.setHeaders(con.getHeaderFields());
        result.setResponse(response.toString());
        result.setResponseCode(String.valueOf(responseCode));
        result.setResponseMessage(con.getResponseMessage());
	String content_type = con.getContentType();
	if (content_type == null || content_type.isEmpty()) {
	    log.warn("null or emtpy content-type url: " + url.toString());
	    content_type = "application/json;charset=UTF-8";
	}
        result.setContentType(content_type);
        result.setSuccess(isSuccessCode(responseCode));
        result.setSentBytes(sentBytes);
        result.setContentEncoding(getEncoding(content_type));

        return result;

    }

    public String getEncoding(String content_type) {
        String[] values = content_type.split(";"); // values.length should be 2
        String charset = "";

        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        return charset;
    }


    public List<DataSegment> extractSegmentUris(String playlistUrl) {
        String pattern = "EXTINF:(\\d+\\.?\\d*).*\\n(#.*:.*\\n)*(.*(\\?.*\\n*)?)";
        final List<DataSegment> mediaList = new ArrayList<>();
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(playlistUrl);

        while (m.find()) {
            DataSegment data = new DataSegment(m.group(1), m.group(3));
            mediaList.add(data);
        }
        return mediaList;
    }

    private int resolutionCompare(String r1, String r2) {
        String[] r1Dimensions = r1.split("x");
        String[] r2Dimensions = r2.split("x");
        int a1 = Integer.parseInt(r1Dimensions[0]) * Integer.parseInt(r1Dimensions[1]);
        int a2 = Integer.parseInt(r2Dimensions[0]) * Integer.parseInt(r2Dimensions[1]);
        return Integer.compare(a1, a2);
    }

    private boolean resolutionOK(String streamResolution, String currentResolution, String matchMode, String customResolution) {
        log.info("resolutionOK: " + streamResolution + ", " + currentResolution + ", " + matchMode + ", " + currentResolution);

        if (matchMode.equalsIgnoreCase(MediaPlaylistSampler.CUSTOM)) {
            if (customResolution != null) {
                return customResolution.equals(streamResolution);
            }
            log.error("selection mode is customResolution, but no custom resolution set");
            return false;
        } else if (matchMode.equalsIgnoreCase(MediaPlaylistSampler.MIN)) {
            if (currentResolution == null) {
                return true;
            } else {
                if (streamResolution == null) return false;
                return (resolutionCompare(streamResolution, currentResolution) <= 0);
            }
        } else if (matchMode.equalsIgnoreCase(MediaPlaylistSampler.MAX)) {
            if (currentResolution == null) {
                return true;
            } else {
                if (streamResolution == null) return false;
                return (resolutionCompare(streamResolution, currentResolution) >= 0);
            }

        }
        log.error("unknown resolution selection mode");
        return false;
    }

    public String selectAudioPlaylist(String playlistData, String customSelector) {
        String audioPattern = "#EXT-X-MEDIA:TYPE=AUDIO(.*)URI=\"(.*\\.m3u8.*)\"";

        return getMediaUri(audioPattern, playlistData, customSelector);
    }

    public String selectSubtitlesPlaylist(String playlistData, String customSelector) {
        String subtitlesPattern = "#EXT-X-MEDIA:TYPE=SUBTITLES(.*)URI=\"(.*\\.m3u8.*)\"";

        return getMediaUri(subtitlesPattern, playlistData, customSelector);
    }

    public String selectVideoPlaylist(String playlistData, String customResolution, String customBandwidth, String bwSelected, String resSelected) {
        String streamPattern = "(EXT-X-STREAM-INF.*)\\n(.*\\.m3u8.*)";
        String bandwidthPattern = "[:|,]\\W*BANDWIDTH=(\\d+)";
        String resolutionPattern = "[:|,]\\W*RESOLUTION=(\\d+x\\d+)";

        return getVideoUri(streamPattern, bandwidthPattern, resolutionPattern, playlistData, customResolution, customBandwidth, bwSelected, resSelected);
    }

    private String getMediaUri(String mediaPattern, String playlistData, String customSelector) {
        Pattern pattern = Pattern.compile(mediaPattern);
        Matcher matcher = pattern.matcher(playlistData);

        if (customSelector == null || customSelector.length() == 0) {
            customSelector = "DEFAULT=YES";
        }

        while (matcher.find()) {
            if (matcher.group(1).contains(customSelector)) {
                return matcher.group(2);
            }
        }
        return null;
    }

    public String getVideoUri(String streamPattern, String bandwidthPattern, String resolutionPattern, String playlistData,
                              String customResolution, String customBandwidth, String bwSelected, String resSelected) {
        String curBandwidth = null;
        String curResolution = null;
        String uri = null;

        Pattern s = Pattern.compile(streamPattern);
        Matcher m = s.matcher(playlistData);

        Pattern b = Pattern.compile(bandwidthPattern);
        Pattern r = Pattern.compile(resolutionPattern);

	String playlist_entry = "<null>";
        while (m.find()) {
	    playlist_entry = m.group(1);
            Matcher mr = r.matcher(playlist_entry);
            boolean rfound = mr.find();
            Matcher mb = b.matcher(playlist_entry);
            boolean bfound = mb.find();

            if (!bfound) {
                log.error("unable to identify bandwidth");
                continue;
            }

            if (bwSelected.equalsIgnoreCase(MediaPlaylistSampler.CUSTOM)) {
                if (Integer.parseInt(mb.group(1)) == Integer.parseInt(customBandwidth)) {
                    if (resolutionOK((rfound ? mr.group(1) : null), curResolution, resSelected, customResolution)) {
                        curResolution = (rfound ? mr.group(1) : null);
                        uri = m.group(2);
                    }
                }
            } else if (bwSelected.equalsIgnoreCase(MediaPlaylistSampler.MIN)) {
                if (curBandwidth == null || (Integer.parseInt(mb.group(1)) <= Integer.parseInt(curBandwidth))) {
                    curBandwidth = mb.group(1);
                    if (resolutionOK((rfound ? mr.group(1) : null), curResolution, resSelected, customResolution)) {
                        curResolution = (rfound ? mr.group(1) : null);
                        uri = m.group(2);
                    }
                }

            } else if (bwSelected.equalsIgnoreCase(MediaPlaylistSampler.MAX)) {
                if (curBandwidth == null || (Integer.parseInt(mb.group(1)) >= Integer.parseInt(curBandwidth))) {
                    curBandwidth = mb.group(1);
                    if (resolutionOK((rfound ? mr.group(1) : null), curResolution, resSelected, customResolution)) {
                        curResolution = (rfound ? mr.group(1) : null);
                        uri = m.group(2);
                    }
                }

            } else {
                log.error("unknown bandwidth selection mode");
            }

        }

	log.info("getVideoUri selected playlist entry: " + playlist_entry);
	log.info("getVideoUri return: " + uri);
        return uri;
    }

    public boolean isLive(String playlistData) {
        String pattern1 = "EXT-X-ENDLIST";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(playlistData);

        return !m1.find();
    }

    public int getTargetDuration(String playlistData) {
        String pattern1 = "EXT-X-TARGETDURATION:(\\d+)";
        Pattern r1 = Pattern.compile(pattern1);
        Matcher m1 = r1.matcher(playlistData);
        if (m1.find()) {
            String dur = m1.group(1);
            return Integer.parseInt(dur);
        }
        return -1;
    }

    protected boolean isSuccessCode(int code) {
        return code >= 200 && code <= 399;
    }

}
