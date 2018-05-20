package com.ramp.jmeter.hls_player.gui;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import java.awt.event.ItemEvent;

public class HlsSamplerPanel extends JPanel {

    private JPanel videoPanel = new JPanel();

    private JPanel videoStreamSelectionPanel = new JPanel();
    private JPanel resolutionOptions = new JPanel();
    private JPanel bandwidthOptions = new JPanel();

    private JPanel audioStreamSelectionPanel = new JPanel();
    private JPanel audioOptions = new JPanel();
    private JPanel closedCaptionOptions = new JPanel();

    private JLabel urlFieldLabel = new JLabel("URL  ");

    private JTextField urlField = new JTextField();
    private JTextField resolutionField = new JTextField();
    private JTextField playSecondsField = new JTextField();
    private JTextField bandwidthField = new JTextField();
    private JTextField audioField = new JTextField();
    private JTextField CCField = new JTextField();


    private JRadioButton rPlayVideoBtn = new JRadioButton("Whole Video");
    private JRadioButton rPlayPartBtn = new JRadioButton("Fixed Duration (sec):");

    private JRadioButton rMinimumResolution = new JRadioButton("Min");
    private JRadioButton rMaximumResolution = new JRadioButton("Max");
    private JRadioButton rCustomResolution = new JRadioButton("Custom (WxH): ");

    private JRadioButton rMinimumBandwidth = new JRadioButton("Min");
    private JRadioButton rMaximumBandwidth = new JRadioButton("Max");
    private JRadioButton rCustomBandwidth = new JRadioButton("Custom (bps): ");

    private JRadioButton rNoAudio = new JRadioButton("None");
    private JRadioButton rDefaultAudio = new JRadioButton("Default");
    private JRadioButton rCustomAudio = new JRadioButton("Custom: ");

    private JRadioButton rNoCC = new JRadioButton("None");
    private JRadioButton rDefaultCC = new JRadioButton("Default");
    private JRadioButton rCustomCC = new JRadioButton("Custom: ");
    
    private ButtonGroup durationGroup = new ButtonGroup();
    private ButtonGroup resolGroup = new ButtonGroup();
    private ButtonGroup bandGroup = new ButtonGroup();
    private ButtonGroup audioGroup = new ButtonGroup();
    private ButtonGroup CCGroup = new ButtonGroup();

    public HlsSamplerPanel() {
        initComponents();
    }

    private void initComponents() {

        urlFieldLabel.setLabelFor(urlField);

        durationGroup.add(rPlayVideoBtn);
        durationGroup.add(rPlayPartBtn);
        rPlayPartBtn.setSelected(true);

        resolGroup.add(rCustomResolution);
        resolGroup.add(rMaximumResolution);
        resolGroup.add(rMinimumResolution);
        rMinimumResolution.setSelected(true);

        bandGroup.add(rCustomBandwidth);
        bandGroup.add(rMinimumBandwidth);
        bandGroup.add(rMaximumBandwidth);
        rMinimumBandwidth.setSelected(true);

        audioGroup.add(rCustomAudio);
        audioGroup.add(rDefaultAudio);
        audioGroup.add(rNoAudio);
        rNoAudio.setSelected(true);

        CCGroup.add(rCustomCC);
        CCGroup.add(rDefaultCC);
        CCGroup.add(rNoCC);
        rNoCC.setSelected(true);

        playSecondsField.setEnabled(true);
        rPlayPartBtn.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        playSecondsField.setEnabled(true);
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        playSecondsField.setEnabled(false);
                    }
                }
        );
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

        videoPanel.setBorder(BorderFactory.createTitledBorder("Video"));
        GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanelLayout.setAutoCreateContainerGaps(true);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
                videoPanelLayout.createSequentialGroup()
                        .addComponent(urlFieldLabel)
                        .addComponent(urlField)
                        .addComponent(rPlayVideoBtn)
                        .addComponent(rPlayPartBtn)
                        .addComponent(playSecondsField)
        );
        videoPanelLayout.setVerticalGroup(
                videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(urlFieldLabel)
                        .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rPlayVideoBtn)
                        .addComponent(rPlayPartBtn)
                        .addComponent(playSecondsField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

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

        audioOptions.setBorder(BorderFactory.createTitledBorder("Audio Track Selection"));
        GroupLayout audioOptionsLayout = new javax.swing.GroupLayout(audioOptions);
        audioOptionsLayout.setAutoCreateContainerGaps(true);
        audioOptions.setLayout(audioOptionsLayout);
        audioOptionsLayout.setHorizontalGroup(
                audioOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rNoAudio)
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
                        .addComponent(rNoAudio)
                        .addGroup(audioOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rDefaultAudio)
                                .addComponent(rCustomAudio)
                                .addComponent(audioField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        closedCaptionOptions.setBorder(BorderFactory.createTitledBorder("CC Track Selection"));
        GroupLayout closedCaptionOptionsLayout = new javax.swing.GroupLayout(closedCaptionOptions);
        closedCaptionOptionsLayout.setAutoCreateContainerGaps(true);
        closedCaptionOptions.setLayout(closedCaptionOptionsLayout);
        closedCaptionOptionsLayout.setHorizontalGroup(
                closedCaptionOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(rNoCC)
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
                        .addComponent(rNoCC)
                        .addGroup(closedCaptionOptionsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(rDefaultCC)
                                .addComponent(rCustomCC)
                                .addComponent(CCField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        )
        );

        videoStreamSelectionPanel.setBorder(BorderFactory.createTitledBorder("Video Stream Selection"));
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

        audioStreamSelectionPanel.setBorder(BorderFactory.createTitledBorder("Audio Stream Selection"));
        GroupLayout audioStreamSelectionLayout = new javax.swing.GroupLayout(audioStreamSelectionPanel);
        audioStreamSelectionLayout.setAutoCreateGaps(true);
        audioStreamSelectionLayout.setAutoCreateContainerGaps(true);
        audioStreamSelectionPanel.setLayout(audioStreamSelectionLayout);
        audioStreamSelectionLayout.setHorizontalGroup(
                audioStreamSelectionLayout.createSequentialGroup()
                        .addComponent(audioOptions)
                        .addComponent(closedCaptionOptions)
                );
        audioStreamSelectionLayout.setVerticalGroup(
                audioStreamSelectionLayout.createParallelGroup()
                .addComponent(audioOptions)
                .addComponent(closedCaptionOptions)
        );


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(videoPanel)
                        .addComponent(videoStreamSelectionPanel)
                        .addComponent(audioStreamSelectionPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(videoPanel)
                        .addComponent(videoStreamSelectionPanel)
                        .addComponent(audioStreamSelectionPanel)
        );

    }

    public void setUrlData(String urlData) {
        urlField.setText(urlData);
    }

    public String getUrlData() {
        return urlField.getText();
    }

    public void setResData(String resData) {
        resolutionField.setText(resData);
    }

    public String getResData() {
        return resolutionField.getText();
    }

    public void setNetData(String netData) {
        bandwidthField.setText(netData);
    }

    public String getNetData() {
        return bandwidthField.getText();
    }

    public void setPlaySecondsData(String seconds) {
        playSecondsField.setText(seconds);
    }

    public String getPlaySecondsData() {
        return playSecondsField.getText();
    }

    public void setVideoDuration(boolean check) {
        rPlayPartBtn.setSelected(check);
    }

    public boolean getVideoDuration() {
        return rPlayPartBtn.isSelected();
    }

    public void setVideoType(String check) {

        //doesn't do anything anymore. Video type determined by playlist
    }

    public void setResolutionType(String check) {
        if (check.equalsIgnoreCase("minResolution"))
            rMinimumResolution.setSelected(true);
        else if (check.equalsIgnoreCase("maxResolution"))
            rMaximumResolution.setSelected(true);
        else
            rCustomResolution.setSelected(true);

    }

    public void setBandwidthType(String check) {
        if (check.equalsIgnoreCase("minBandwidth"))
            rMinimumBandwidth.setSelected(true);
        else if (check.equalsIgnoreCase("maxBandwidth"))
            rMaximumBandwidth.setSelected(true);
        else
            rCustomBandwidth.setSelected(true);

    }

    public String isChecked() {
        if (rPlayVideoBtn.isSelected()) {

            return "-1";

        } else {

            return getPlaySecondsData();
        }
    }

    public String getResolutionType() {
        if (rCustomResolution.isSelected()) {
            return "customResolution";
        } else if (rMinimumResolution.isSelected()) {
            return "minResolution";
        } else
            return "maxResolution";
    }

    public String getBandwidthType() {
        if (rCustomBandwidth.isSelected()) {
            return "customBandwidth";
        } else if (rMinimumBandwidth.isSelected()) {
            return "minBandwidth";
        } else
            return "maxBandwidth";
    }

    public String videoType() {
        //This method is not supported. video type is determined by the playlist
        return "vod";
    }

    public boolean rDurationVideoButtoncheck() {
        return rPlayVideoBtn.isSelected();
    }

}
