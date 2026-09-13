package dev.jreact.dom;

import java.util.HashMap;

public class Props extends HashMap<String, String> {

    public static Props p() {
        return new Props();
    }

    public Props css(String classes) {
        this.put("class", classes);
        return this; 
    }

    public Props id(String id) {
        this.put("id", id);
        return this;
    }
    
    public Props type(String type) {
        this.put("type", type);
        return this;
    }
    
}
