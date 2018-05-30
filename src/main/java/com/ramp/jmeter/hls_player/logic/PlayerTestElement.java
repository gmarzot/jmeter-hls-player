package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.testelement.AbstractTestElement;

import java.util.ArrayList;


public class PlayerTestElement extends AbstractTestElement{

    ArrayList<MediaPlaylistSampler> MediaPlaylistSamplerPanels = new ArrayList<>();

    public PlayerTestElement(){
        super();
        setName("HLS Player");
        //addTestElement(new MediaPlaylistSampler());
    }
}
