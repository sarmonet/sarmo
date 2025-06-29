package com.sarmo.authservice.security.oauth2;

import java.util.Map;

public class FacebookOAuth2UserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getImageUrl() {
        if (attributes.containsKey("picture")) {
            Map pictureObj = (Map) attributes.get("picture");
            if (pictureObj.containsKey("data")) {
                Map dataObj = (Map) pictureObj.get("data");
                if (dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}