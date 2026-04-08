package com.eduardomarinho.cnpjutils.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
    
    private int requests = 20;
    private int minutes = 1;
    private boolean enabled = true;
    
    public int getRequests() {
        return requests;
    }
    
    public void setRequests(int requests) {
        this.requests = requests;
    }
    
    public int getMinutes() {
        return minutes;
    }
    
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
