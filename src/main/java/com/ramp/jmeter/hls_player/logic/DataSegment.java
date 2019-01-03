package com.ramp.jmeter.hls_player.logic;

import java.util.Objects;

public class DataSegment {
	private String duration;
	private String tsUri;

	public DataSegment(String _duration, String _tsUri) {
		this.duration = _duration;
		this.tsUri = _tsUri;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUri() {
		return tsUri;
	}

	public void setTsUri(String tsUri) {
		this.tsUri = tsUri;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if (obj instanceof DataSegment){
			DataSegment seg = (DataSegment) obj;
			return Objects.equals(duration, seg.duration) && Objects.equals(tsUri,seg.tsUri);
		} else{
			return false;
		}
	}
}
