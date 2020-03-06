package com.yudachi.model.crawler.core.proxy;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 代理IP实体类
 */
@Data
public class CrawlerProxy implements Serializable {


    public CrawlerProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    private String host;

    private Integer port;


    /**
     * 获取代理信息
     *
     * @return
     */
    public String getProxyInfo() {

        return this.host + ":" + port;
    }

    @Override
    public String toString() {
        return "CrawlerProxy{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrawlerProxy that = (CrawlerProxy) o;
        return host.equals(that.host) &&
                port.equals(that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

}
