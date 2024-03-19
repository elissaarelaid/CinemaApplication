package net.cinemaApplication.backend.model.movieSession;

import lombok.Getter;

@Getter
public enum Language {
    ESTONIAN("Estonian"),
    RUSSIAN("Russian"),
    ENGLISH("English");

    private String languageName;
    private Language(String languageName) {
        this.languageName = languageName;
    }
}
