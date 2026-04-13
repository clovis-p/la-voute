<template>
  <div class="flex min-h-screen">
    <div class="hidden md:flex w-1/2 bg-(--p-primary-500) flex-col items-center justify-center gap-4 p-12">
      <h1 class="text-white text-4xl font-bold text-center">{{ titleMessage }}</h1>
      <h2 class="text-white/80 text-xl text-center">{{ subtitleMessage }}</h2>
    </div>
    <div class="flex w-full md:w-1/2 items-center justify-center">
      <LoginForm v-if="!showRegisterForm" @switch-to-register="showRegisterForm = true" />
      <RegisterForm v-else @switch-to-login="showRegisterForm = false" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import LoginForm from "./Login/LoginForm.vue";
import RegisterForm from "./Login/RegisterForm.vue";

const titleMessage = ref('');
const subtitleMessage = ref('');

const showRegisterForm = ref(false);

onMounted(async () => {
  const response = await axios.get('/api/home');
  titleMessage.value = response.data.titleMessage;
  subtitleMessage.value = response.data.subtitleMessage;
});
</script>
