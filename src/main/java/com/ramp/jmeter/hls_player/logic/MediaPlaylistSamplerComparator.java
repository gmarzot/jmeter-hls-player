package com.ramp.jmeter.hls_player.logic;

import java.util.Comparator;

class MediaPlaylistSamplerComparator implements Comparator<MediaPlaylistSampler> {
    @Override
    public int compare(MediaPlaylistSampler o1, MediaPlaylistSampler o2) {
        return Long.compare(o1.getNextCallTimeMillis(), o2.getNextCallTimeMillis());
    }
}
