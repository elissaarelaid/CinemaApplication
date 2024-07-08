import genre from "./genre.ts";
import ageLimit from "./ageLimit.ts";

export interface Movie {
    id: number,
    title: string,
    description: string,
    movieLength: number,
    genre: genre,
    rating: number,
    ageLimit: ageLimit,
    director: string,
}
