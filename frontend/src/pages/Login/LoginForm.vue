<template>
  <div class="flex flex-col gap-8 w-full max-w-sm p-8 md:p-12">
      <div class="flex flex-col items-center gap-4">
        <div class="flex items-center gap-4">
          <AppLogo class="h-20 w-20" />
        </div>
      </div>
      <div class="flex flex-col gap-6 w-full">
        <div class="flex flex-col gap-2 w-full">
          <InputText id="email1" type="text" placeholder="Nom d'utilisateur" class="w-full px-3 py-2 shadow-sm rounded-lg" />
        </div>
        <div class="flex flex-col gap-2 w-full">
          <Password id="password1" v-model="password" placeholder="Mot de passe" :toggleMask="true" :feedback="false" input-class="w-full!" />
        </div>
        <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between w-full gap-3 sm:gap-0">
          <div class="flex items-center gap-2">
            <Checkbox id="rememberme1" v-model="checked1" :binary="true" />
            <label for="rememberme1" class="text-surface-900 dark:text-surface-0 leading-normal">Remember me</label>
          </div>
        </div>
      </div>
      <Button label="Se connecter" icon="pi pi-user" class="w-full py-2 rounded-lg flex justify-center items-center gap-2">
        <template #icon>
          <i class="pi pi-user text-base! leading-normal!" />
        </template>
      </Button>
    <Button label="Créer un compte" icon="pi pi-user" severity="secondary" class="w-full py-2 rounded-lg flex justify-center items-center gap-2" @click="emit('switch-to-register')" />
  </div>
</template>

<script setup>
import AppLogo from '@/components/AppLogo.vue';
import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';

import { ref } from 'vue';

defineProps(['title', 'subtitle']);
const emit = defineEmits(['switch-to-register']);

const password = ref('');
const checked1 = ref(true);
</script>
