package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.samplers.SampleResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class MediaPlaylistSamplerTest {


    private MediaPlaylistSampler sampler;
    private Parser parserMock;

    @Before
    public void setup()
            throws Exception {
//        TestJMeterUtils.createJmeterEnv();

        parserMock = Mockito.mock(Parser.class);
        sampler = new MediaPlaylistSampler();
//		sampler.setURLData("http://www.mock.com/path");
        sampler.setResData("640x360");
        sampler.setNetworkData("1395723");
        sampler.setBandwidthType(MediaPlaylistSampler.MAX);
        sampler.setResolutionType(MediaPlaylistSampler.CUSTOM);
//		sampler.setPlaySecondsData("20");
//		sampler.setVideoDuration(true);
        sampler.setParser(parserMock);
        sampler.setName("Test");
    }

    @Test
    public void testSample() throws Exception {

        RequestInfo respond1 = new RequestInfo();
        RequestInfo respond2 = new RequestInfo();
        RequestInfo respond3 = new RequestInfo();
        RequestInfo respond4 = new RequestInfo();
        RequestInfo respond5 = new RequestInfo();
        SegmentInfo f1 = new SegmentInfo("10", "https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_1.ts");
        SegmentInfo f2 = new SegmentInfo("10", "https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_2.ts");
        SegmentInfo f3 = new SegmentInfo("10", "https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_3.ts");
        List<SegmentInfo> fragments = new ArrayList<SegmentInfo>();
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);

        String payload1 = "#EXTM3U\n#EXT-X-VERSION:4\n#EXT-X-STREAM-INF:AUDIO=\"600k\",BANDWIDTH=1395723,PROGRAM-ID=1,CODECS=\"avc1.42c01e,mp4a.40.2\",RESOLUTION=640x360,SUBTITLES=\"subs\"\n/videos/DianaLaufenberg_2010X/video/600k.m3u8?preroll=Thousands&uniqueId=4df94b1d\n#EXT-X-STREAM-INF:AUDIO=\"600k\",BANDWIDTH=170129,PROGRAM-ID=1,CODECS=\"avc1.42c00c,mp4a.40.2\",RESOLUTION=320x180,SUBTITLES=\"subs\"\n/videos/DianaLaufenberg_2010X/video/64k.m3u8?preroll=Thousands&uniqueId=4df94b1d\n#EXT-X-STREAM-INF:AUDIO=\"600k\",BANDWIDTH=425858,PROGRAM-ID=1,CODECS=\"avc1.42c015,mp4a.40.2\",RESOLUTION=512x288,SUBTITLES=\"subs\"\n/videos/DianaLaufenberg_2010X/video/180k.m3u8?preroll=Thousands&uniqueId=4df94b1d\n#EXT-X-STREAM-INF:AUDIO=\"600k\",BANDWIDTH=718158,PROGRAM-ID=1,CODECS=\"avc1.42c015,mp4a.40.2\",RESOLUTION=512x288,SUBTITLES=\"subs\"\n/videos/DianaLaufenberg_2010X/video/320k.m3u8?preroll=Thousands&uniqueId=4df94b1d";
        String payload2 = "#EXTM3U\n#EXT-X-TARGETDURATION:10\n#EXT-X-VERSION:4\n#EXT-X-MEDIA-SEQUENCE:0\n#EXT-X-PLAYLIST-TYPE:VOD\n#EXTINF:5.0000,\n#EXT-X-BYTERANGE:440672@0\nhttps://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k.ts\n#EXTINF:5.0000,\n#EXT-X-BYTERANGE:94000@440672\nhttps://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k.ts\n#EXTINF:1.9583,\n#EXT-X-BYTERANGE:22748@534672\nhttps://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k.ts\n#EXT-X-DISCONTINUITY";
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

        respond1.setRequestHeaders("GET  http://www.mock.com/path\n");
        respond1.url = "http://www.mock.com/path";
        respond1.setHeaders(headers);
        respond1.setResponse(payload1);
        respond1.setResponseCode("200");
        respond1.setResponseMessage("OK");
        respond1.setContentType("application/json;charset=UTF-8");
        respond1.setSuccess(true);
        respond1.setSentBytes(payload1.length());
        respond1.setContentEncoding("UTF-8");

        respond2.setRequestHeaders("GET  http://www.mock.com/path/videos/DianaLaufenberg_2010X/video/600k.m3u8?preroll=Thousands&uniqueId=4df94b1d\n");
        respond2.url = "http://www.mock.com/path/videos/DianaLaufenberg_2010X/video/600k.m3u8?preroll=Thousands&uniqueId=4df94b1d";
        respond2.setHeaders(headers);
        respond2.setResponse(payload2);
        respond2.setResponseCode("200");
        respond2.setResponseMessage("OK");
        respond2.setContentType("application/json;charset=UTF-8");
        respond2.setSuccess(true);
        respond2.setSentBytes(payload2.length());
        respond2.setContentEncoding("UTF-8");

        respond3.setRequestHeaders("GET  https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_1.ts\n");
        respond3.setHeaders(headers);
        respond3.setResponse("chunck");
        respond3.setResponseCode("200");
        respond3.setResponseMessage("OK");
        respond3.setContentType("application/json;charset=UTF-8");
        respond3.setSuccess(true);
        respond3.setSentBytes("chunck".length());
        respond3.setContentEncoding("UTF-8");

        respond4.setRequestHeaders("GET  https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_2.ts\n");
        respond4.setHeaders(headers);
        respond4.setResponse("chunck");
        respond4.setResponseCode("200");
        respond4.setResponseMessage("OK");
        respond4.setContentType("application/json;charset=UTF-8");
        respond4.setSuccess(true);
        respond4.setSentBytes("chunck".length());
        respond4.setContentEncoding("UTF-8");

        respond5.setRequestHeaders("GET  https://pb.tedcdn.com/bumpers/hls/video/in/Thousands-320k_3.ts\n");
        respond5.setHeaders(headers);
        respond5.setResponse("chunck");
        respond5.setResponseCode("200");
        respond5.setResponseMessage("OK");
        respond5.setContentType("application/json;charset=UTF-8");
        respond5.setSuccess(true);
        respond5.setSentBytes("chunck".length());
        respond5.setContentEncoding("UTF-8");

        Mockito.when(parserMock.getBaseUrl(Mockito.any(URL.class), Mockito.any(SampleResult.class), Mockito.anyBoolean()))
                .thenReturn(respond1)
                .thenReturn(respond2)
                .thenReturn(respond3)
                .thenReturn(respond4)
                .thenReturn(respond5);

        Mockito.when(parserMock.selectVideoPlaylist(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn("/videos/DianaLaufenberg_2010X/video/600k.m3u8?preroll=Thousands&uniqueId=4df94b1d");
        Mockito.when(parserMock.extractSegmentUris(Mockito.any()))
                .thenReturn(fragments);
        Mockito.when(parserMock.isLive(Mockito.any(String.class)))
                .thenReturn(false);


        assertFalse(sampler == null);

        sampler.setProperty("HLS.MEDIA_PLAYLIST_TYPE", "Video");
        sampler.setMasterPlaylist(respond1);
        SampleResult result = sampler.sample(null);

        assertFalse(result == null);
        assertEquals("GET  http://www.mock.com/path\n\n\n\n\n", result.getRequestHeaders());
        assertEquals(true, result.isSuccessful());
        assertEquals("OK", result.getResponseMessage());
        assertEquals("playlist", result.getSampleLabel());
        assertEquals("headerKey1 : header11 header12 header13\nheaderKey2 : header21 header22 header23\nheaderKey3 : header31\n", result.getResponseHeaders());
        assertEquals(new String(payload1.getBytes(), StandardCharsets.UTF_8), new String(result.getResponseData(), StandardCharsets.UTF_8));
        assertEquals("200", result.getResponseCode());
        assertEquals("application/json;charset=UTF-8", result.getContentType());
        assertEquals("UTF-8", result.getDataEncodingNoDefault());

    }
}
