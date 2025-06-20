package de.hipp.pnp.api.dto;

import org.springframework.stereotype.Component;

@Component
public class LanguageRequest {
    String locale;
    Integer gameType;

    public LanguageRequest(Integer gameType, String locale) {
        this.gameType = gameType;
        this.locale = locale;
    }

    public LanguageRequest() {
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getGameType() {
        return gameType;
    }

    public void setGameType(Integer gameType) {
        this.gameType = gameType;
    }
}
