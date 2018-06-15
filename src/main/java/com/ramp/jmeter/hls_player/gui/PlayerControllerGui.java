package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.PlayerController;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.awt.*;

public class PlayerControllerGui extends AbstractControllerGui {
    private static final Logger log = LoggingManager.getLoggerForClass();


    private MasterPlaylistPanel masterPlaylistPanel;

    public PlayerControllerGui(){
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
    public void configure(TestElement testElement){
        super.configure(testElement);
        PlayerController playerController = (PlayerController) testElement;
        //Master Playlist Setup
        masterPlaylistPanel.urlTextField.setText(playerController.getPropertyAsString(PlayerController.MASTER_PLAYLIST_URL));
        masterPlaylistPanel.customDurationRButton.setSelected(playerController.getPropertyAsBoolean(PlayerController.IS_CUSTOM_DURATION));
        masterPlaylistPanel.customDurationTextField.setText(playerController.getPropertyAsString(PlayerController.CUSTOM_DURATION));

        masterPlaylistPanel.repaint();
        masterPlaylistPanel.revalidate();
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        this.configureTestElement(testElement);
        if (testElement instanceof PlayerController) {
            //Master Playlist Saving
            testElement.setProperty(PlayerController.MASTER_PLAYLIST_URL, masterPlaylistPanel.urlTextField.getText());
            testElement.setProperty(PlayerController.IS_CUSTOM_DURATION, masterPlaylistPanel.customDurationRButton.isSelected());
            testElement.setProperty(PlayerController.CUSTOM_DURATION, masterPlaylistPanel.customDurationTextField.getText());

        }
    }
}
