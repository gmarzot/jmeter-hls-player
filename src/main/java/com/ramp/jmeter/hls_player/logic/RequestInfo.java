package com.ramp.jmeter.hls_player.logic;

import java.util.List;
import java.util.Map;

public class RequestInfo {
    private String url;
    private Map<String, List<String>> headers;
    private String response;
    private String responseCode;
    private String responseMessage;
    private String contentType;
    private boolean success;
    private long sentBytes;
    private String contentEncoding;
    private String requestHeaders;

    Map<String, List<String>> getHeaders() {
        return headers;
    }


    String getUrl(){
        return url;
    }

    void setUrl(String urlString){
        url = urlString;
    }


    void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    /**
     * @return Returns the Header.
     */

    String getHeadersAsString() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            res.append(entry.getKey()).append(" :");
            for (String value : entry.getValue()) {
                res.append(" ").append(value);
            }
            res.append("\n");

        }
        return res.toString();

    }

    /**
     * @return Returns the Response.
     */
    String getResponse() {
        return response;
    }

    void setResponse(String response) {
        this.response = response;
    }

    /**
     * @return Returns the ResponseCode.
     */
    String getResponseCode() {
        return responseCode;
    }

    void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return Returns the ResponseMessage.
     */
    String getResponseMessage() {
        return responseMessage;
    }

    void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return Returns the ContentType.
     */
    String getContentType() {
        return contentType;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return Returns the Success.
     */
    boolean isSuccess() {
        return success;
    }

    void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return Returns the Sent Bytes.
     */
    long getSentBytes() {
        return sentBytes;
    }

    void setSentBytes(long sentBytes) {
        this.sentBytes = sentBytes;
    }

    /**
     * @return Returns the Content Encoding.
     */
    String getContentEncoding() {
        return contentEncoding;
    }

    void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * @return Returns the Request Headers.
     */
    String getRequestHeaders() {
        return requestHeaders;
    }

    void setRequestHeaders(String setRequestHeaders) {
        this.requestHeaders = setRequestHeaders;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return (this.headers.equals(((RequestInfo) obj).headers) &&
                this.response.equals(((RequestInfo) obj).response) &&
                this.responseCode.equals(((RequestInfo) obj).responseCode) &&
                this.responseMessage.equals(((RequestInfo) obj).responseMessage) &&
                this.contentType.equals(((RequestInfo) obj).contentType) &&
                this.success == ((RequestInfo) obj).success &&
                this.sentBytes == ((RequestInfo) obj).sentBytes &&
                this.contentEncoding.equals(((RequestInfo) obj).contentEncoding) &&
                this.requestHeaders.equals(((RequestInfo) obj).requestHeaders));
    }

}
