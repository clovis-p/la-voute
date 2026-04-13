import { createRouter, createWebHistory } from 'vue-router';
import Home from './pages/Home.vue';
import Accueil from './pages/Accueil.vue';

const routes = [
    { path: '/', component: Home },
    { path: '/accueil', component: Accueil },
];

export default createRouter({
    history: createWebHistory(),
    routes,
});
