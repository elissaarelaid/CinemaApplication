package net.cinemaApplication.backend.entity.movie;

import lombok.Getter;

@Getter
public enum AgeLimit {
    FAMILY("Family movie"),
    OVER_16("Over 16 years"),
    OVER_14("Over 14 years"),
    NO_LIMIT("No age limit");

    private String ageLimitName;
    private AgeLimit(String ageLimitName){
        this.ageLimitName = ageLimitName;
    }
}
