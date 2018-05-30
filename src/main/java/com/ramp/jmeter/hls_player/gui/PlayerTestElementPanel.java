package com.ramp.jmeter.hls_player.gui;

import javax.swing.*;
import java.util.ArrayList;

public class PlayerTestElementPanel extends JPanel {


    //Master Playlist Panel
    MasterPlaylistPanel masterPlaylistPanel = new MasterPlaylistPanel();

    //Media Playlist Selector
    JButton mediaPlaylistPanelButton = new JButton("Add...");

    //Media Playlist Panel
    ArrayList<MediaPlaylistSamplerPanel> mediaPlaylistPanels = new ArrayList<>();

    public PlayerTestElementPanel() {
        initComponents();
        setupLayouts();
    }

    private void initComponents() {
        //Actions
        mediaPlaylistPanelButton.addActionListener(e -> {
            mediaPlaylistPanels.add(new MediaPlaylistSamplerPanel());
            this.removeAll();

            setupLayouts();
            repaint();
            revalidate();
        });
    }

    private void setupLayouts() {
        //Layouts
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.setLayout(layout);
        {
            GroupLayout.ParallelGroup pg = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
            pg.addComponent(masterPlaylistPanel);
            for (JPanel panel :
                    mediaPlaylistPanels) {
                pg.addComponent(panel);
            }
            pg.addComponent(mediaPlaylistPanelButton);
            layout.setHorizontalGroup(pg);
        }
        {
            GroupLayout.SequentialGroup sg = layout.createSequentialGroup();
            sg.addComponent(masterPlaylistPanel);
            for (JPanel panel :
                    mediaPlaylistPanels) {
                sg.addComponent(panel);
            }
            sg.addComponent(mediaPlaylistPanelButton,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE);
            layout.setVerticalGroup(sg);
        }
    }
}
