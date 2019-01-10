package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.PlayerController;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class PlayerControllerGui extends AbstractControllerGui {
    private static final Logger log = LoggerFactory.getLogger(PlayerControllerGui.class);


    private final MasterPlaylistPanel masterPlaylistPanel;

    public PlayerControllerGui() {
        super();
        masterPlaylistPanel = new MasterPlaylistPanel();
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        this.add(makeTitlePanel(), BorderLayout.NORTH);
        this.add(masterPlaylistPanel, BorderLayout.CENTER);
    }

    @Override
    public String getLabelResource() {
        return "HLS Player";
    }

    @Override
    public String getStaticLabel() {
        return getLabelResource();
    }

    @Override
    public TestElement createTestElement() {
        PlayerController sampler = new PlayerController();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void configure(TestElement testElement) {
        super.configure(testElement);
        PlayerController playerController = (PlayerController) testElement;
        //Master Playlist Setup
        masterPlaylistPanel.setUrlTextField(playerController.getURLData());
        masterPlaylistPanel.setIsCustomDuration(playerController.isCustomDuration());
        masterPlaylistPanel.setCustomDurationTextField(playerController.getCustomDuration());

        masterPlaylistPanel.repaint();
        masterPlaylistPanel.revalidate();
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        this.configureTestElement(testElement);
        if (testElement instanceof PlayerController) {
            //Master Playlist Saving
            testElement.setProperty(PlayerController.MASTER_PLAYLIST_URL, masterPlaylistPanel.getUrlTextField());
            testElement.setProperty(PlayerController.IS_CUSTOM_DURATION, masterPlaylistPanel.isCustomDuration());
            testElement.setProperty(PlayerController.CUSTOM_DURATION, masterPlaylistPanel.getCustomDurationTextField());

        }
    }
}
