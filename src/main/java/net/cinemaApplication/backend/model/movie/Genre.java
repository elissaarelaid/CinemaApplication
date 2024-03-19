package net.cinemaApplication.backend.model.movie;

public enum Genre {
    ROMANCE("Romance"),
    THRILLER("Thriller"),
    HORROR("Horror"),
    ANIMATION("Animation"),
    DOCUMENTARY("Documentary"),
    ACTION("Action"),
    HISTORICAL("Historical"),
    COMEDY("Comedy"),
    PSYCHOLOGICAL("Psychological"),
    ADVENTURE("Adventure"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    SCIENCE_FICTION("Science-fiction");
    private String genreName;
    private Genre(String genreName){
        this.genreName = genreName;
    }
}
