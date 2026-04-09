<template>
  <div>
    <h1>{{ titleMessage }}</h1>
    <h2>{{ subtitleMessage }}</h2>
    <Button>piton lol !!</Button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const titleMessage = ref('');
const subtitleMessage = ref('');

import Button from "primevue/button";

onMounted(async () => {
  const response = await axios.get('/api/home');
  titleMessage.value = response.data.titleMessage;
  subtitleMessage.value = response.data.subtitleMessage;
});
</script>
