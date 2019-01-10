package com.ramp.jmeter.hls_player.gui;

import com.ramp.jmeter.hls_player.logic.MediaPlaylistSampler;

import javax.swing.*;
import java.awt.event.ItemEvent;

class MediaPlaylistSamplerPanel extends JPanel {

    private final JPanel videoStreamSelectionPanel = new JPanel();
    private final JPanel resolutionOptions = new JPanel();
    private final JPanel bandwidthOptions = new JPanel();

    private JPanel mediaPlaylistPanel = videoStreamSelectionPanel;

    private final JPanel audioTrackSelectionPanel = new JPanel();

    private final JPanel closedCaptionTrackSelectionPanel = new JPanel();

    private final JTextField resolutionField = new JTextField();
    private final JTextField bandwidthField = new JTextField();
    private final JTextField audioField = new JTextField();
    private final JTextField CCField = new JTextField();

    private final JComboBox mediaPlaylistTypeCBox = new JComboBox<>(new String[]{"Video", "Audio", "Closed Captions"});

    private final JRadioButton rMinimumResolution = new JRadioButton("Min");
    private final JRadioButton rMaximumResolution = new JRadioButton("Max");
    private final JRadioButton rCustomResolution = new JRadioButton("Custom (WxH): ");

    private final JRadioButton rMinimumBandwidth = new JRadioButton("Min");
    private final JRadioButton rMaximumBandwidth = new JRadioButton("Max");
    private final JRadioButton rCustomBandwidth = new JRadioButton("Custom (bps): ");

    private final JRadioButton rDefaultAudio = new JRadioButton("Default");
    private final JRadioButton rCustomAudio = new JRadioButton("Custom: ");

    private final JRadioButton rDefaultCC = new JRadioButton("Default");
    private final JRadioButton rCustomCC = new JRadioButton("Custom: ");

    private final ButtonGroup resolutionButtonGroup = new ButtonGroup();
    private final ButtonGroup bandGroup = new ButtonGroup();
    private final ButtonGroup audioGroup = new ButtonGroup();
    private final ButtonGroup CCGroup = new ButtonGroup();


    public MediaPlaylistSamplerPanel() {
        initComponents();
        setupLayouts();
    }

    private void initComponents() {

        resolutionButtonGroup.add(rCustomResolution);
        resolutionButtonGroup.add(rMaximumResolution);
        resolutionButtonGroup.add(rMinimumResolution);
        rMinimumResolution.setSelected(true);

        bandGroup.add(rCustomBandwidth);
        bandGroup.add(rMinimumBandwidth);
        bandGroup.add(rMaximumBandwidth);
        rMinimumBandwidth.setSelected(true);

        audioGroup.add(rCustomAudio);
        audioGroup.add(rDefaultAudio);
        rDefaultAudio.setSelected(true);

        CCGroup.add(rCustomCC);
        CCGroup.add(rDefaultCC);
        rDefaultCC.setSelected(true);

        bandwidthField.setEnabled(false);
        rCustomBandwidth.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        bandwidthField.setEnabled(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        bandwidthField.setEnabled(false);
                    }
                }
        );
        resolutionField.setEnabled(false);
        rCustomResolution.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        resolutionField.setEnabled(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        resolutionField.setEnabled(false);
                    }
                }
        );
        audioField.setEnabled(false);
        rCustomAudio.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        audioField.setEnabled(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        audioField.setEnabled(false);
                    }
                }
        );
        CCField.setEnabled(false);
        rCustomCC.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        CCField.setEnabled(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        CCField.setEnabled(false);
                    }
                }
        );
        mediaPlaylistTypeCBox.addItemListener( (e) -> {
            String mediaType = (String) e.getItem();
            this.removeAll();
            switch (mediaType) {
                case "Video":
                    mediaPlaylistPanel = videoStreamSelectionPanel;
                    break;
                case "Audio":
                    mediaPlaylistPanel = audioTrackSelectionPanel;
                    break;
                case "Closed Captions":
                    mediaPlaylistPanel = closedCaptionTrackSelectionPanel;
                    break;
                default:
                    //not good
                    break;
            }
            setupLayouts();
            repaint();
            revalidate();
        });
    }

    private void setupLayouts() {
        bandwidthOptions.setBorder(BorderFactory.createTitledBorder("Bandwidth Options"));
        GroupLayout bandwidthOptionsLayout = new javax.swing.GroupLayout(bandwidthOptions);
        bandwidthOptionsLayout.setAutoCreateContainerGaps(true);
        bandwidthOptions.setLayout(bandwidthOptionsLayout);
        bandwidthOptionsLayout.setHorizontalGroup(
                bandwidthOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rMinimumBandwidth)
                        .addComponent(rMaximumBandwidth)
                        .addGroup(bandwidthOptionsLayout.createSequentialGroup()
                                .addComponent(rCustomBandwidth)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bandwidthField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        bandwidthOptionsLayout.setVerticalGroup(
                bandwidthOptionsLayout.createSequentialGroup()
                        .addComponent(rMinimumBandwidth)
                        .addComponent(rMaximumBandwidth)
                        .addGroup(bandwidthOptionsLayout.createParallelGroup()
                                .addComponent(rCustomBandwidth)
                                .addComponent(bandwidthField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        resolutionOptions.setBorder(BorderFactory.createTitledBorder("Resolution Options"));
        GroupLayout resolutionOptionsLayout = new javax.swing.GroupLayout(resolutionOptions);
        resolutionOptionsLayout.setAutoCreateContainerGaps(true);
        resolutionOptions.setLayout(resolutionOptionsLayout);
        resolutionOptionsLayout.setHorizontalGroup(
                resolutionOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rMinimumResolution)
                        .addComponent(rMaximumResolution)
                        .addGroup(resolutionOptionsLayout.createSequentialGroup()
                                .addComponent(rCustomResolution)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resolutionField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        resolutionOptionsLayout.setVerticalGroup(
                resolutionOptionsLayout.createSequentialGroup()
                        .addComponent(rMinimumResolution)
                        .addComponent(rMaximumResolution)
                        .addGroup(resolutionOptionsLayout.createParallelGroup()
                                .addComponent(rCustomResolution)
                                .addComponent(resolutionField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        audioTrackSelectionPanel.setBorder(BorderFactory.createTitledBorder("Track Selection"));
        GroupLayout audioOptionsLayout = new javax.swing.GroupLayout(audioTrackSelectionPanel);
        audioOptionsLayout.setAutoCreateContainerGaps(true);
        audioTrackSelectionPanel.setLayout(audioOptionsLayout);
        audioOptionsLayout.setHorizontalGroup(
                audioOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(audioOptionsLayout.createSequentialGroup()
                                .addComponent(rDefaultAudio)
                                .addComponent(rCustomAudio)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(audioField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        audioOptionsLayout.setVerticalGroup(
                audioOptionsLayout.createSequentialGroup()
                        .addGroup(audioOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rDefaultAudio)
                                .addComponent(rCustomAudio)
                                .addComponent(audioField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        closedCaptionTrackSelectionPanel.setBorder(BorderFactory.createTitledBorder("Track Selection"));
        GroupLayout closedCaptionOptionsLayout = new javax.swing.GroupLayout(closedCaptionTrackSelectionPanel);
        closedCaptionOptionsLayout.setAutoCreateContainerGaps(true);
        closedCaptionTrackSelectionPanel.setLayout(closedCaptionOptionsLayout);
        closedCaptionOptionsLayout.setHorizontalGroup(
                closedCaptionOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(closedCaptionOptionsLayout.createSequentialGroup()
                                .addComponent(rDefaultCC)
                                .addComponent(rCustomCC)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CCField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        closedCaptionOptionsLayout.setVerticalGroup(
                closedCaptionOptionsLayout.createSequentialGroup()
                        .addGroup(closedCaptionOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(rDefaultCC)
                                .addComponent(rCustomCC)
                                .addComponent(CCField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        videoStreamSelectionPanel.setBorder(BorderFactory.createTitledBorder("Stream Selection"));
        GroupLayout videoStreamSelectionLayout = new javax.swing.GroupLayout(videoStreamSelectionPanel);
        videoStreamSelectionLayout.setAutoCreateGaps(true);
        videoStreamSelectionLayout.setAutoCreateContainerGaps(true);
        videoStreamSelectionPanel.setLayout(videoStreamSelectionLayout);
        videoStreamSelectionLayout.setHorizontalGroup(
                videoStreamSelectionLayout.createSequentialGroup()
                        .addComponent(bandwidthOptions)
                        .addComponent(resolutionOptions)
        );
        videoStreamSelectionLayout.setVerticalGroup(
                videoStreamSelectionLayout.createParallelGroup()
                        .addComponent(bandwidthOptions)
                        .addComponent(resolutionOptions)
        );


        this.setBorder(BorderFactory.createTitledBorder("Media Playlist"));
        GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(mediaPlaylistTypeCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
                        .addComponent(mediaPlaylistPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mediaPlaylistTypeCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
                        .addComponent(mediaPlaylistPanel)
        );
    }


    String getMediaPlaylistType() {
        return (String) mediaPlaylistTypeCBox.getSelectedItem();
    }

    void setMediaPlaylistType(String type) {
        mediaPlaylistTypeCBox.setSelectedItem(type);
    }

    //old functions
    public void setCustomResolution(String resData) {
        resolutionField.setText(resData);
    }

    public String getCustomResolution() {
        return resolutionField.getText();
    }

    public void setCustomBandwidth(String netData) {
        bandwidthField.setText(netData);
    }

    public String getCustomBandwidth() {
        return bandwidthField.getText();
    }


    public void setResolutionType(String check) {
        if (check.equals(MediaPlaylistSampler.MIN))
            rMinimumResolution.setSelected(true);
        else if (check.equals(MediaPlaylistSampler.MAX))
            rMaximumResolution.setSelected(true);
        else
            rCustomResolution.setSelected(true);

    }

    public void setBandwidthType(String check) {
        if (check.equals(MediaPlaylistSampler.MIN))
            rMinimumBandwidth.setSelected(true);
        else if (check.equals(MediaPlaylistSampler.MAX))
            rMaximumBandwidth.setSelected(true);
        else
            rCustomBandwidth.setSelected(true);

    }

    public String getResolutionType() {
        if (rCustomResolution.isSelected()) {
            return MediaPlaylistSampler.CUSTOM;
        } else if (rMinimumResolution.isSelected()) {
            return MediaPlaylistSampler.MIN;
        } else
            return MediaPlaylistSampler.MAX;
    }

    public String getBandwidthType() {
        if (rCustomBandwidth.isSelected()) {
            return MediaPlaylistSampler.CUSTOM;
        } else if (rMinimumBandwidth.isSelected()) {
            return MediaPlaylistSampler.MIN;
        } else
            return MediaPlaylistSampler.MAX;
    }


    public boolean isDefaultAudio() {
        return rDefaultAudio.isSelected();
    }

    public String getCustomAudio() {
        return audioField.getText();
    }

    public boolean isDefaultCC() {
        return rDefaultCC.isSelected();
    }

    public String getCustomCC() {
        return CCField.getText();
    }

    public void setDefaultAudio() {
        rDefaultAudio.setSelected(true);
    }

    public void setCustomAudio(String customAudio) {
        rCustomAudio.setSelected(true);
        audioField.setText(customAudio);
    }

    public void setDefaultCC() {
        rDefaultCC.setSelected(true);
    }

    public void setCustomCC(String customCC) {
        rCustomCC.setSelected(true);
        CCField.setText(customCC);
    }
}
