import { createApp } from 'vue';
import './style.css';
import Home from './pages/Home.vue';
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';

const app = createApp(Home);
app.use(PrimeVue, {
    theme: {
        preset: Aura
    }
});
app.mount('#app');