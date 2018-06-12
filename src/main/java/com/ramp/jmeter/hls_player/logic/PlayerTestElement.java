package com.ramp.jmeter.hls_player.logic;

import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.GenericController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.NextIsNullException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class PlayerTestElement extends GenericController {

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String MASTER_PLAYLIST_URL = "MASTER_PLAYLIST_URL";
    public static final String IS_CUSTOM_DURATION = "IS_CUSTOM_DURATION";
    public static final String CUSTOM_DURATION = "CUSTOM_DURATION";

    public static final String CUSTOM = "CUSTOM";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";
    public static final String MEDIA_PLAYLIST_SAMPLER = "MEDIA_PLAYLIST_SAMPLER";
    public static final String SAMPLER_COUNT = "SAMPLER_COUNT";

    protected MediaPlaylistSampler MasterPlaylistSampler;

    private boolean firstLoop = true;
    private long startTime = 0;
    private long timeElapsed = 0;
    private long duration;

    public PlayerTestElement(){
        super();
        setName("HLS Player");


    }

    @Override
    public void initialize(){
        log.debug("initialize");

        for (int i = 0; i < getPropertyAsInt(SAMPLER_COUNT); i++) {
            this.addTestElement((MediaPlaylistSampler) this.getProperty(MEDIA_PLAYLIST_SAMPLER+i).getObjectValue());
        }

        if (getPropertyAsBoolean(IS_CUSTOM_DURATION)){
            duration = getPropertyAsLong(CUSTOM_DURATION);
        } else {
            duration = -1;
        }
        super.initialize();
    }

    @Override
    public Sampler next(){
        log.debug("size of subControllersAndSamplers "+subControllersAndSamplers.size());
        log.debug("duration: "+duration);
        if (firstLoop) {
            startTime = System.currentTimeMillis();
            firstLoop = false;

        }
        if(this.getPropertyAsBoolean(IS_CUSTOM_DURATION)){
            timeElapsed = System.currentTimeMillis() - startTime;
            if(timeElapsed > duration*1000){
                this.setDone(true);
                log.debug("out of time!");
            }
        }


        this.fireIterEvents();
        log.debug("Calling next on: PlayerTestElement");
        Sampler returnValue = null;
        log.debug("isDone: "+isDone());

        returnValue = super.next();

        if(returnValue==null) log.debug("sampler was null");

        return returnValue;
    }

    @Override
    protected Sampler nextIsNull() {
        log.debug("nextIsNull");
        this.reInitialize();
        log.debug("After reinitialize, isDone: "+this.isDone());
        return this.isDone() ? null : this.next();
    }


}
