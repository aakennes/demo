package demo.NetManage;

import java.util.*;

public class Message {
    private final String type;
    private final Map<String, String> fields;
    Message(String type, Map<String, String> fields) {
        this.type = type == null ? "" : type;
        this.fields = fields == null ? Collections.emptyMap() : Collections.unmodifiableMap(fields);
    }
    public String getType() { 
        return type; 
    }
    public Map<String,String> getFields() { 
        return fields; 
    }
    public String get(String key) { 
        return fields.get(key); 
    }

    @Override 
    public String toString() { 
        return "Message[type=" + type + ",fields=" + fields + "]"; 
    }
}