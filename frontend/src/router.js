import { createRouter, createWebHistory } from 'vue-router';
import axios from 'axios';
import Login from './pages/Login.vue';
import Home from './pages/Home.vue';
import Download from './pages/Download.vue';

const routes = [
  { path: '/', component: Login, meta: { requiresGuest: true } },
  { path: '/accueil', component: Home, meta: { requiresAuth: true } },
  { path: '/download/:fileId', component: Download },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to) => {
  let loggedIn = false;
  try {
    await axios.get('/api/user/me');
    loggedIn = true;
  } catch {}

  if (to.meta.requiresAuth && !loggedIn) {
    window.location.replace('/');
    return false;
  }
  if (to.meta.requiresGuest && loggedIn) return '/accueil';
});

export default router;
