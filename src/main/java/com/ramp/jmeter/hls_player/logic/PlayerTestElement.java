package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class PlayerTestElement extends LoopController {

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String MASTER_PLAYLIST_URL = "MASTER_PLAYLIST_URL";
    public static final String IS_CUSTOM_DURATION = "IS_CUSTOM_DURATION";
    public static final String CUSTOM_DURATION = "CUSTOM_DURATION";

    public static final String CUSTOM = "CUSTOM";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";
    public static final String MEDIA_PLAYLIST_SAMPLER = "MEDIA_PLAYLIST_SAMPLER";
    public static final String SAMPLER_COUNT = "SAMPLER_COUNT";

    public ArrayList<MediaPlaylistSampler> MediaPlaylistSamplers = new ArrayList<>();

    public PlayerTestElement(){
        super();
        setName("HLS Player");
    }




}
