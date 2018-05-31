package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.PlayerTestElement;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.awt.*;
import java.util.Iterator;

public class PlayerTestElementGui extends AbstractSamplerGui{
    private static final Logger log = LoggingManager.getLoggerForClass();

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
        super.configure(el);
        PlayerTestElement playerTestElement = (PlayerTestElement) el;
        //Master Playlist Setup
        PlayerTestElementPanel.masterPlaylistPanel.urlTextField.setText(playerTestElement.getPropertyAsString("MASTER_PLAYLIST_URL"));
        PlayerTestElementPanel.masterPlaylistPanel.wholeDurationRButton.setSelected(playerTestElement.getPropertyAsBoolean("IS_WHOLE_VIDEO"));
        PlayerTestElementPanel.masterPlaylistPanel.customDurationRButton.setSelected(!playerTestElement.getPropertyAsBoolean("IS_WHOLE_VIDEO"));
        PlayerTestElementPanel.masterPlaylistPanel.customDurationTextField.setText(playerTestElement.getPropertyAsString("CUSTOM_DURATION"));

        for (int i = 0; isNextMediaPlaylistSamplerSettings(playerTestElement, i); i++){
            log.info("Configuring gui for media playlist #"+i);
            MediaPlaylistSamplerPanel panel = PlayerTestElementPanel.mediaPlaylistPanels.get(i);
            if (panel == null){
                panel = new MediaPlaylistSamplerPanel();
                PlayerTestElementPanel.mediaPlaylistPanels.add(panel);
            }
            panel.setMediaPlaylistType(playerTestElement.getPropertyAsString(i+"_MEDIA_PLAYLIST_TYPE"));
            switch (panel.getMediaPlaylistType()) {
                case "Video":
                    panel.setBandwidthType(playerTestElement.getPropertyAsString(i+"_BANDWIDTH_OPTION"));
                    if(panel.getBandwidthType().equals("customBandwidth")){
                        panel.setCustomBandwidth(playerTestElement.getPropertyAsString(i+"_CUSTOM_BANDWIDTH"));
                    }
                    panel.setResolutionType(playerTestElement.getPropertyAsString(i+"_RESOLUTION_OPTION"));
                    if(panel.getResolutionType().equals("customResolution")){
                        panel.setCustomResolution(playerTestElement.getPropertyAsString(i+"_CUSTOM_RESOLUTION"));
                    }
                    break;
                case "Audio":
                    String customAudio = playerTestElement.getPropertyAsString(i+"_CUSTOM_AUDIO", "def");
                    if(customAudio.equals("def"))
                        panel.setDefaultAudio();
                    else
                        panel.setCustomAudio(customAudio);
                    break;
                case "Closed Captions":
                    String customCC = playerTestElement.getPropertyAsString(i+"_CUSTOM_CC", "def");
                    if(customCC.equals("def"))
                        panel.setDefaultCC();
                    else
                        panel.setCustomCC(customCC);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isNextMediaPlaylistSamplerSettings(PlayerTestElement playerTestElement, int index) {
        return (playerTestElement.getPropertyAsString(index + "_MEDIA_PLAYLIST_TYPE", "def") != "def");
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        this.configureTestElement(testElement);
        if (testElement instanceof PlayerTestElement) {
            //Master Playlist Saving
            testElement.setProperty("MASTER_PLAYLIST_URL", PlayerTestElementPanel.masterPlaylistPanel.urlTextField.getText());
            testElement.setProperty("IS_WHOLE_VIDEO", PlayerTestElementPanel.masterPlaylistPanel.wholeDurationRButton.isSelected());
            testElement.setProperty("CUSTOM_DURATION", PlayerTestElementPanel.masterPlaylistPanel.customDurationTextField.getText());

            //Media Playlist Saving
            Iterator<MediaPlaylistSamplerPanel> panelIterator = PlayerTestElementPanel.mediaPlaylistPanels.iterator();
            for (int i = 0; panelIterator.hasNext(); i++) {
                log.info("Saving from gui for media playlist #"+i);
                MediaPlaylistSamplerPanel panel = panelIterator.next();
                testElement.setProperty(i+"_MEDIA_PLAYLIST_TYPE",panel.getMediaPlaylistType());
                switch (panel.getMediaPlaylistType()) {
                    case "Video":
                        testElement.setProperty(i+"_BANDWIDTH_OPTION",panel.getBandwidthType());
                        if(panel.getBandwidthType().equals("customBandwidth")){
                            testElement.setProperty(i+"_CUSTOM_BANDWIDTH",panel.getCustomBandwidth());
                        }
                        testElement.setProperty(i+"_RESOLUTION_OPTION",panel.getResolutionType());
                        if(panel.getResolutionType().equals("customResolution")){
                            testElement.setProperty(i+"_CUSTOM_RESOLUTION",panel.getCustomResolution());
                        }
                        break;
                    case "Audio":
                        if(!panel.isDefaultAudio()){
                            testElement.setProperty(i+"_CUSTOM_AUDIO",panel.getCustomAudio());
                        }
                        break;
                    case "Closed Captions":
                        if(!panel.isDefaultCC()){
                            testElement.setProperty(i+"_CUSTOM_CC",panel.getCustomCC());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
