<script setup>
import {Dialog} from "primevue";

const props = defineProps(['visible', 'file']);
const emit = defineEmits(['close']);
</script>

<template>
  <Dialog :visible="props.visible" modal :header="`Partager ${props.file?.name}`" :style="{ width: '30rem' }" @update:visible="emit('close')">
  </Dialog>
</template>
