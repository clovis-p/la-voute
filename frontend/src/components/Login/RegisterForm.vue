<template>
  <div class="flex flex-col gap-8 w-full max-w-sm p-8 md:p-12">
      <div class="flex flex-col items-center gap-4">
        <div class="flex items-center gap-4">
          <AppLogo class="h-20 w-20" />
        </div>
      </div>
      <div class="flex flex-col gap-6 w-full">
        <div class="flex flex-col gap-2 w-full">
          <InputText id="first-name" v-model="firstName" type="text" placeholder="Prénom" class="w-full px-3 py-2 shadow-sm rounded-lg" />
        </div>
        <div class="flex flex-col gap-2 w-full">
          <InputText id="last-name" v-model="lastName" type="text" placeholder="Nom" class="w-full px-3 py-2 shadow-sm rounded-lg" />
        </div>
        <div class="flex flex-col gap-2 w-full">
          <InputText id="username" v-model="username" type="text" placeholder="Nom d'utilisateur" class="w-full px-3 py-2 shadow-sm rounded-lg" />
        </div>
        <div class="flex flex-col gap-2 w-full">
          <Password id="password1" v-model="password" placeholder="Mot de passe" :toggleMask="true" :feedback="false" input-class="w-full!" />
        </div>
        <div class="flex flex-col gap-2 w-full">
          <Password id="password2" v-model="confirmPassword" placeholder="Confirmer le mot de passe" :toggleMask="true" :feedback="false" input-class="w-full!" />
        </div>
      </div>
      <p v-if="errorMessage" class="text-red-500 text-sm text-center whitespace-pre-line">{{ errorMessage }}</p>
      <Button label="S'inscrire" icon="pi pi-user" class="w-full py-2 rounded-lg flex justify-center items-center gap-2" :loading="loading" @click="handleRegister">
        <template #icon>
          <i class="pi pi-user text-base! leading-normal!" />
        </template>
      </Button>
    <Button label="Retour" icon="pi pi-user" severity="secondary" class="w-full py-2 rounded-lg flex justify-center items-center gap-2" @click="emit('switch-to-login')" />
  </div>
</template>

<script setup>
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import AppLogo from '@/assets/logo.svg';
import axios from 'axios';

import { ref } from 'vue';

defineProps(['title', 'subtitle']);
const emit = defineEmits(['switch-to-login']);

const firstName = ref('');
const lastName = ref('');
const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const errorMessage = ref('');
const loading = ref(false);

async function handleRegister() {
  errorMessage.value = '';

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Les mots de passe ne correspondent pas.';
    return;
  }

  loading.value = true;
  try {
    await axios.post('/api/register', {
      firstName: firstName.value,
      lastName: lastName.value,
      username: username.value,
      password: password.value,
    });
    emit('switch-to-login');
  } catch (err) {
    errorMessage.value = err.response?.data?.message ?? 'Une erreur est survenue.';
  } finally {
    loading.value = false;
  }
}
</script>
