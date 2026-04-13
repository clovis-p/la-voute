import { createRouter, createWebHistory } from 'vue-router';
import Login from './pages/Login.vue';
import Home from './pages/Home.vue';

const routes = [
    { path: '/', component: Login },
    { path: '/accueil', component: Home },
];

export default createRouter({
    history: createWebHistory(),
    routes,
});
