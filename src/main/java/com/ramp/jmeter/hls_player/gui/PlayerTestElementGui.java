package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.MediaPlaylistSampler;
import com.ramp.jmeter.hls_player.logic.PlayerTestElement;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.JMeterThread;
import org.apache.jmeter.util.keystore.JmeterKeyStore;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.awt.*;
import java.util.Iterator;

public class PlayerTestElementGui extends AbstractSamplerGui{
    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String DEFAULT = "DEFAULT";

    private PlayerTestElementPanel PlayerTestElementPanel;

    public PlayerTestElementGui() {
        super();
        init();
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        this.add(makeTitlePanel(), BorderLayout.NORTH);
        this.add(PlayerTestElementPanel, BorderLayout.CENTER);
    }

    void init(){
        PlayerTestElementPanel = new PlayerTestElementPanel();
    }
    @Override
    public String getStaticLabel() {
        return "HLS Player";
    }

    @Override
    public String getLabelResource() {
        return "HLS Player";
    }

    @Override
    public TestElement createTestElement() {
        PlayerTestElement element = new PlayerTestElement();
    modifyTestElement(element);
        return element;
    }

    @Override
    public void configure(TestElement el) {
        log.info("CONFIGURE");
        super.configure(el);
        PlayerTestElement playerTestElement = (PlayerTestElement) el;
        //Master Playlist Setup
        PlayerTestElementPanel.masterPlaylistPanel.urlTextField.setText(playerTestElement.getPropertyAsString(PlayerTestElement.MASTER_PLAYLIST_URL));
        PlayerTestElementPanel.masterPlaylistPanel.customDurationRButton.setSelected(playerTestElement.getPropertyAsBoolean(PlayerTestElement.IS_CUSTOM_DURATION));
        PlayerTestElementPanel.masterPlaylistPanel.customDurationTextField.setText(playerTestElement.getPropertyAsString(PlayerTestElement.CUSTOM_DURATION));


        Iterator<MediaPlaylistSamplerPanel> panelIterator = PlayerTestElementPanel.mediaPlaylistPanels.iterator();
        for (int i =0;i < playerTestElement.getPropertyAsInt(PlayerTestElement.SAMPLER_COUNT); i ++) {
            MediaPlaylistSampler sampler = (MediaPlaylistSampler) playerTestElement.getProperty(PlayerTestElement.MEDIA_PLAYLIST_SAMPLER+i).getObjectValue();
            log.info("Configuring gui for media playlist");
            MediaPlaylistSamplerPanel panel;
            if(panelIterator!=null&&panelIterator.hasNext())
                panel = panelIterator.next();
            else {
                panelIterator = null;
                panel = new MediaPlaylistSamplerPanel();
                PlayerTestElementPanel.mediaPlaylistPanels.add(panel);
            }
            panel.setMediaPlaylistType(sampler.getPropertyAsString(MediaPlaylistSampler.MEDIA_PLAYLIST_TYPE));
            switch (panel.getMediaPlaylistType()) {
                case MediaPlaylistSampler.TYPE_VIDEO:
                    panel.setBandwidthType(sampler.getPropertyAsString(MediaPlaylistSampler.BANDWIDTH_OPTION));
                    if(panel.getBandwidthType().equals(MediaPlaylistSampler.CUSTOM)){
                        panel.setCustomBandwidth(sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_BANDWIDTH));
                    }
                    panel.setResolutionType(sampler.getPropertyAsString(MediaPlaylistSampler.RESOLUTION_OPTION));
                    if(panel.getResolutionType().equals(MediaPlaylistSampler.CUSTOM)){
                        panel.setCustomResolution(sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_RESOLUTION));
                    }
                    break;
                case MediaPlaylistSampler.TYPE_AUDIO:
                    String customAudio = sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_AUDIO, DEFAULT);
                    if(customAudio.equals(DEFAULT))
                        panel.setDefaultAudio();
                    else
                        panel.setCustomAudio(customAudio);
                    break;
                case MediaPlaylistSampler.TYPE_CLOSED_CAPTIONS:
                    String customCC = sampler.getPropertyAsString(MediaPlaylistSampler.CUSTOM_CC, DEFAULT);
                    if(customCC.equals(DEFAULT))
                        panel.setDefaultCC();
                    else
                        panel.setCustomCC(customCC);
                    break;
                default:
                    break;
            }
        }


        PlayerTestElementPanel.setupLayouts();
        PlayerTestElementPanel.repaint();
        PlayerTestElementPanel.revalidate();
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        log.info("MODIFY_TEST_ELEMENT");
        this.configureTestElement(testElement);
        if (testElement instanceof PlayerTestElement) {
            //Master Playlist Saving
            testElement.setProperty(PlayerTestElement.MASTER_PLAYLIST_URL, PlayerTestElementPanel.masterPlaylistPanel.urlTextField.getText());
            testElement.setProperty(PlayerTestElement.IS_CUSTOM_DURATION, PlayerTestElementPanel.masterPlaylistPanel.customDurationRButton.isSelected());
            testElement.setProperty(PlayerTestElement.CUSTOM_DURATION, PlayerTestElementPanel.masterPlaylistPanel.customDurationTextField.getText());

            //Media Playlist Saving
            Iterator<MediaPlaylistSamplerPanel> panelIterator = PlayerTestElementPanel.mediaPlaylistPanels.iterator();
            PlayerTestElement playerTestElement = (PlayerTestElement) testElement;
            Iterator<MediaPlaylistSampler> samplerIterator = playerTestElement.MediaPlaylistSamplers.iterator();
            int i;
            for (i = 0;panelIterator.hasNext(); i++) {
                log.info("Saving from gui for media playlist");
                MediaPlaylistSamplerPanel panel = panelIterator.next();
                MediaPlaylistSampler sampler;
                if (samplerIterator!=null && samplerIterator.hasNext())
                    sampler = samplerIterator.next();
                else {
                    samplerIterator = null;
                    sampler = new MediaPlaylistSampler();
                    this.configureTestElement(sampler);
                    playerTestElement.MediaPlaylistSamplers.add(sampler);
                }

                sampler.setProperty(MediaPlaylistSampler.MEDIA_PLAYLIST_TYPE,panel.getMediaPlaylistType());
                switch (panel.getMediaPlaylistType()) {
                    case MediaPlaylistSampler.TYPE_VIDEO:
                        sampler.setProperty(MediaPlaylistSampler.BANDWIDTH_OPTION,panel.getBandwidthType());
                        if(panel.getBandwidthType().equals(MediaPlaylistSampler.CUSTOM)){
                            sampler.setProperty(MediaPlaylistSampler.CUSTOM_BANDWIDTH,panel.getCustomBandwidth());
                        }
                        sampler.setProperty(MediaPlaylistSampler.RESOLUTION_OPTION,panel.getResolutionType());
                        if(panel.getResolutionType().equals(MediaPlaylistSampler.CUSTOM)){
                            sampler.setProperty(MediaPlaylistSampler.CUSTOM_RESOLUTION,panel.getCustomResolution());
                        }
                        break;
                    case MediaPlaylistSampler.TYPE_AUDIO:
                        if(!panel.isDefaultAudio()){
                            sampler.setProperty(MediaPlaylistSampler.CUSTOM_AUDIO,panel.getCustomAudio());
                        }
                        break;
                    case MediaPlaylistSampler.TYPE_CLOSED_CAPTIONS:
                        if(!panel.isDefaultCC()){
                            sampler.setProperty(MediaPlaylistSampler.CUSTOM_CC,panel.getCustomCC());
                        }
                        break;
                    default:
                        break;
                }
                playerTestElement.setProperty(new TestElementProperty(PlayerTestElement.MEDIA_PLAYLIST_SAMPLER+i,sampler));
            }
            playerTestElement.setProperty(PlayerTestElement.SAMPLER_COUNT,i);
        }
    }
}
