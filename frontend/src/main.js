import { createApp } from 'vue';
import './style.css';
import 'primeicons/primeicons.css';
import App from './App.vue';
import PrimeVue from 'primevue/config';
import Aura from '@primeuix/themes/aura';
import {definePreset} from "@primeuix/themes";
import router from './router.js';
import axios from 'axios';

axios.interceptors.request.use(async config => {
    const isMutating = ['post', 'put', 'patch', 'delete'].includes(config.method);
    const hasCsrfCookie = document.cookie.split(';').some(c => c.trim().startsWith('XSRF-TOKEN='));
    if (isMutating && !hasCsrfCookie) {
        let response = await axios.get('/api/csrf');
        if (response.status !== 200) {
            await router.push("/login");
        }
    }
    return config;
});

const app = createApp(App);

const customPreset = definePreset(Aura, {
    semantic: {
        primary: {
            50: '{purple.50}',
            100: '{purple.100}',
            200: '{purple.200}',
            300: '{purple.300}',
            400: '{purple.400}',
            500: '{purple.500}',
            600: '{purple.600}',
            700: '{purple.700}',
            800: '{purple.800}',
            900: '{purple.900}',
            950: '{purple.950}'
        }
    }
});

app.use(PrimeVue, {
    theme: {
        preset: customPreset
    }
});
app.use(router);
app.mount('#app');