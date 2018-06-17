package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.MediaPlaylistSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;

public class MediaPlaylistSamplerGui extends AbstractSamplerGui {

    private static final String DEFAULT = "DEFAULT";

    private MediaPlaylistSamplerPanel panel;

    public MediaPlaylistSamplerGui() {
        super();
        panel = new MediaPlaylistSamplerPanel();
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        this.add(makeTitlePanel(), BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    public String getLabelResource() {
        return "HLS Media Playlist Sampler";
    }

    @Override
    public String getStaticLabel() {
        return getLabelResource();
    }

    @Override
    public TestElement createTestElement() {
        MediaPlaylistSampler sampler = new MediaPlaylistSampler();
        configure(sampler);
        return sampler;
    }

    @Override
    public void configure(TestElement testElement) {
        super.configure(testElement);
        MediaPlaylistSampler sampler = (MediaPlaylistSampler) testElement;
        panel.setMediaPlaylistType(sampler.getPropertyAsString(MediaPlaylistSampler.MEDIA_PLAYLIST_TYPE));
        switch (panel.getMediaPlaylistType()) {
            case MediaPlaylistSampler.TYPE_VIDEO:
                panel.setBandwidthType(sampler.getPropertyAsString(MediaPlaylistSampler.BANDWIDTH_OPTION));
                if (panel.getBandwidthType().equals(MediaPlaylistSampler.CUSTOM)) {
                    panel.setCustomBandwidth(sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_BANDWIDTH));
                }
                panel.setResolutionType(sampler.getPropertyAsString(MediaPlaylistSampler.RESOLUTION_OPTION));
                if (panel.getResolutionType().equals(MediaPlaylistSampler.CUSTOM)) {
                    panel.setCustomResolution(sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_RESOLUTION));
                }
                break;
            case MediaPlaylistSampler.TYPE_AUDIO:
                String customAudio = sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_AUDIO, DEFAULT);
                if (customAudio.equals(DEFAULT))
                    panel.setDefaultAudio();
                else
                    panel.setCustomAudio(customAudio);
                break;
            case MediaPlaylistSampler.TYPE_SUBTITLES:
                String customCC = sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_CC, DEFAULT);
                if (customCC.equals(DEFAULT))
                    panel.setDefaultCC();
                else
                    panel.setCustomCC(customCC);
                break;
            default:
                break;
        }
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        this.configureTestElement(testElement);
        MediaPlaylistSampler sampler = (MediaPlaylistSampler) testElement;
        sampler.setProperty(MediaPlaylistSampler.MEDIA_PLAYLIST_TYPE, panel.getMediaPlaylistType());
        switch (panel.getMediaPlaylistType()) {
            case MediaPlaylistSampler.TYPE_VIDEO:
                sampler.setProperty(MediaPlaylistSampler.BANDWIDTH_OPTION, panel.getBandwidthType());
                if (panel.getBandwidthType().equals(MediaPlaylistSampler.CUSTOM)) {
                    sampler.setProperty(MediaPlaylistSampler.CUSTOM_BANDWIDTH, panel.getCustomBandwidth());
                }
                sampler.setProperty(MediaPlaylistSampler.RESOLUTION_OPTION, panel.getResolutionType());
                if (panel.getResolutionType().equals(MediaPlaylistSampler.CUSTOM)) {
                    sampler.setProperty(MediaPlaylistSampler.CUSTOM_RESOLUTION, panel.getCustomResolution());
                }
                break;
            case MediaPlaylistSampler.TYPE_AUDIO:
                if (!panel.isDefaultAudio()) {
                    sampler.setProperty(MediaPlaylistSampler.CUSTOM_AUDIO, panel.getCustomAudio());
                }
                break;
            case MediaPlaylistSampler.TYPE_SUBTITLES:
                if (!panel.isDefaultCC()) {
                    sampler.setProperty(MediaPlaylistSampler.CUSTOM_CC, panel.getCustomCC());
                }
                break;
            default:
                break;
        }
    }
}
