<template>
  <Dialog
    v-model:visible="props.visible"
    modal
    header="Confirmation"
    :style="{ width: '25rem', maxWidth: '90vw' }"
    :close-on-escape="false"
    @keydown.escape.prevent="emit('close')"
  >
    <div class="flex items-center gap-4 mb-4">
      <i class="pi pi-exclamation-triangle text-2xl text-red-500" />
      <span v-if="file">Voulez-vous vraiment supprimer {{ file.name }} ?</span>
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" label="Annuler" severity="secondary" @click="emit('close')" />
      <Button
        type="button"
        label="Supprimer"
        severity="danger"
        :loading="loading"
        @click="handleDelete"
      />
    </div>
  </Dialog>
</template>

<script setup>
import { ref } from 'vue';
import { Button, Dialog } from 'primevue';
import axios from 'axios';

const props = defineProps(['visible', 'file']);
const emit = defineEmits(['close', 'refreshFileList']);
const loading = ref(false);

async function handleDelete() {
  loading.value = true;
  try {
    await axios.delete(`/api/files/${props.file.id}/delete`);
  } finally {
    loading.value = false;
  }
  emit('refreshFileList');
  emit('close');
}
</script>
