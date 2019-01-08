package com.ramp.jmeter.hls_player.gui;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class MasterPlaylistPanel extends JPanel {
    //Master Playlist Panel
    JLabel urlTextFieldLabel = new JLabel("URL:");
    JTextField urlTextField = new JTextField();
    ButtonGroup durationButtons = new ButtonGroup();
    JRadioButton wholeDurationRButton = new JRadioButton("Whole Duration", true);
    JRadioButton customDurationRButton = new JRadioButton("Custom Duration", false);
    JTextField customDurationTextField = new JTextField();


    public MasterPlaylistPanel() {
        //Buttons
        durationButtons.add(wholeDurationRButton);
        durationButtons.add(customDurationRButton);

        //Actions
        customDurationRButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                customDurationTextField.setEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                customDurationTextField.setEnabled(false);
            }
        });

        //Layout
        GroupLayout masterPlaylistPanelLayout = new GroupLayout(this);
        masterPlaylistPanelLayout.setAutoCreateGaps(true);
        masterPlaylistPanelLayout.setAutoCreateContainerGaps(true);
        this.setBorder(BorderFactory.createTitledBorder("Master Playlist"));
        this.setLayout(masterPlaylistPanelLayout);
        masterPlaylistPanelLayout.setHorizontalGroup(
                masterPlaylistPanelLayout.createSequentialGroup()
                        .addComponent(urlTextFieldLabel)
                        .addComponent(urlTextField)
                        .addComponent(wholeDurationRButton)
                        .addComponent(customDurationRButton)
                        .addComponent(customDurationTextField)
        );
        masterPlaylistPanelLayout.setVerticalGroup(
                masterPlaylistPanelLayout.createParallelGroup()
                        .addComponent(urlTextFieldLabel)
                        .addComponent(urlTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(wholeDurationRButton)
                        .addComponent(customDurationRButton)
                        .addComponent(customDurationTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }
}
