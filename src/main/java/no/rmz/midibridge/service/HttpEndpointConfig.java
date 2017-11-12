package no.rmz.midibridge.service;

public class HttpEndpointConfig implements NamedEntity {
    private String id;

    private String path;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
