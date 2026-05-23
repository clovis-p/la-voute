<script setup>
import {useRoute} from "vue-router";
import {onMounted} from "vue";

const route = useRoute();

onMounted(() => {
  const fileId = route.params.fileId;
  window.location.href = `/api/files/${fileId}`;
});
</script>

<template></template>
