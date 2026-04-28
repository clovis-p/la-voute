<script setup>
import Button from "primevue/button";
import {Avatar, Toolbar, SplitButton} from "primevue";
import AppLogo from '@/assets/logo.svg';
import DefaultAvatar from '@/assets/test-images/default-pp.jpg';
import { useRouter } from 'vue-router';
import axios from "axios";

const router = useRouter();

const userMenuItems = [
  {
    label: 'Se déconnecter',
    command: function () {
      let response = axios.post('/logout');
      if (response.status === 200) {
        router.push('/');
      }
      else {
        console.log(response);
      }
    }
  }
];

</script>

<template>
  <div class="p-2">
  <Toolbar>
    <template #start>
      <div class="flex items-center gap-2">
        <Button class="p-0!" text plain @click="router.push('/accueil')">
          <AppLogo class="h-8 w-8" />
        </Button>
      </div>
    </template>

    <template #end>
      <div class="flex items-center gap-2">
        <SplitButton class="user-btn" :model="userMenuItems">
          <span class="flex items-center font-bold">
            <Avatar :image="DefaultAvatar"/>
            <span class="mx-2">Monsieur Test</span>
          </span>
        </SplitButton>
      </div>
    </template>
  </Toolbar>
  </div>
</template>

<style scoped>
@reference "tailwindcss";

.user-btn :deep(.p-button) {
  @apply p-0;
}
</style>