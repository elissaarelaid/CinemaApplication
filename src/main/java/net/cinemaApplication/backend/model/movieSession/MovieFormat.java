package net.cinemaApplication.backend.model.movieSession;

import lombok.Getter;

@Getter
public enum MovieFormat {
    TWO_D("2D"),
    THREE_D("3D");
    private String formatName;
    private MovieFormat(String formatName){
        this.formatName = formatName;
    }
}
