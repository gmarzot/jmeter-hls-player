package com.blazemeter.jmeter.hls.logic;

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

}
