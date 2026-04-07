<template>
  <div>
    <h1>{{ titleMessage }}</h1>
    <h2>{{ subtitleMessage }}</h2>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const titleMessage = ref('');
const subtitleMessage = ref('');

onMounted(async () => {
  const response = await axios.get('/api/home');
  titleMessage.value = response.data.titleMessage;
  subtitleMessage.value = response.data.subtitleMessage;
})
</script>
