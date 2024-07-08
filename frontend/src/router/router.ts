import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router';
import MoviesList from "../views/MoviesList.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Movies',
        component: MoviesList,
        props: { title: 'Movies' },
        meta: { requiresAuth: false },
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
