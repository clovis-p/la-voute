<template>
  <div class="flex flex-col gap-8 w-full max-w-sm p-8 md:p-12">
    <div class="flex flex-col items-center gap-4">
      <div class="flex items-center gap-4">
        <AppLogo class="h-20 w-20" />
      </div>
    </div>

    <p v-if="registered" class="text-green-600 text-sm text-center">Compte créé avec succès ! Vous pouvez maintenant vous connecter.</p>

    <Form v-slot="$form" :resolver="resolver" :initialValues="initialValues" @submit="handleLogin" class="flex flex-col gap-6 w-full">
      <div class="flex flex-col gap-1">
        <InputText name="username" type="text" placeholder="Nom d'utilisateur" class="w-full px-3 py-2 shadow-sm rounded-lg" :invalid="$form.username?.invalid" />
        <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">{{ $form.username.error?.message }}</Message>
      </div>
      <div class="flex flex-col gap-1">
        <Password name="password" placeholder="Mot de passe" :toggleMask="true" :feedback="false" input-class="w-full!" :invalid="$form.password?.invalid" />
        <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">{{ $form.password.error?.message }}</Message>
      </div>
      <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between w-full gap-3 sm:gap-0">
        <div class="flex items-center gap-2">
          <Checkbox v-model="rememberMe" inputId="rememberme" :binary="true" />
          <label for="rememberme" class="text-surface-900 dark:text-surface-0 leading-normal">Se souvenir de moi</label>
        </div>
      </div>
      <Message v-if="serverError" severity="error" size="small" variant="simple">{{ serverError }}</Message>
      <Button type="submit" label="Se connecter" class="w-full py-2 rounded-lg flex justify-center items-center gap-2" :loading="loading">
        <template #icon>
          <i class="pi pi-user text-base! leading-normal!" />
        </template>
      </Button>
    </Form>
    <Button label="Créer un compte" severity="secondary" class="w-full py-2 rounded-lg flex justify-center items-center gap-2" @click="emit('switch-to-register')" />
  </div>
</template>

<script setup>
import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Message from 'primevue/message';
import { Form } from '@primevue/forms';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import AppLogo from '@/assets/AppLogo.vue';
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { z } from 'zod';

defineProps(['title', 'subtitle', 'registered']);
const emit = defineEmits(['switch-to-register']);

const router = useRouter();
const rememberMe = ref(true);
const serverError = ref('');
const loading = ref(false);

const initialValues = { username: '', password: '' };

const schema = z.object({
  username: z.string().min(1, 'Le nom d\'utilisateur est requis.'),
  password: z.string().min(1, 'Le mot de passe est requis.'),
});

const resolver = zodResolver(schema);

async function handleLogin({ valid, values }) {
  if (!valid) return;

  serverError.value = '';
  loading.value = true;
  try {
    await axios.get('/api/csrf');
    const params = new URLSearchParams();
    params.append('username', values.username);
    params.append('password', values.password);
    await axios.post('/login', params);
    router.push('/accueil');
  } catch (err) {
    if (err.response?.status === 401) {
      serverError.value = 'Identifiants invalides.';
    } else {
      serverError.value = 'Une erreur est survenue.';
    }
  } finally {
    loading.value = false;
  }
}
</script>
