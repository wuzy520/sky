package com.wuzy.sky.registry;

public class InstanceDetails {
    private String id;

    private String listenAddress;

    private int listenPort;

    private String interfaceName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstanceDetails details = (InstanceDetails) o;

        return id != null ? id.equals(details.id) : details.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InstanceDetails{" +
                "id='" + id + '\'' +
                ", listenAddress='" + listenAddress + '\'' +
                ", listenPort=" + listenPort +
                ", interfaceName='" + interfaceName + '\'' +
                '}';
    }
}
