package net.cinemaApplication.backend.entity.movieSession;

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
