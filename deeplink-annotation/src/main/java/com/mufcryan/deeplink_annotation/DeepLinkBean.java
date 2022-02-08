package com.mufcryan.deeplink_annotation;

public class DeepLinkBean {
    private String host;
    private String scheme;
    private String pathPrefix;

    public DeepLinkBean(String host, String scheme, String pathPrefix) {
        this.host = host;
        this.scheme = scheme;
        this.pathPrefix = pathPrefix;
    }

    public String getHost() {
        return host;
    }

    public String getScheme() {
        return scheme;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }
}