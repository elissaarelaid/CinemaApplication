import { defineStore } from 'pinia';
import { Movie } from "../modules/movie";
import {ref, Ref} from "vue";
import useApi from "../modules/api.ts";

export const useMoviesStore = defineStore('moviesStore', () => {
    const movies: Ref<Movie[] | undefined> = ref();
    let allMovies: Movie[] = [];

    const loadMovies = async () => {
        const apiGetMovies = useApi<Movie[]>('movies');

        await apiGetMovies.request();

        if (apiGetMovies.response.value) {
            return apiGetMovies.response.value!;
        }

        return [];
    };

    const load = async () => {
        allMovies = await loadMovies();
        movies.value = allMovies;
    };
    return { movies, load }
})
