<template>
  <Dialog
    v-model:visible="props.visible"
    modal
    header="Danger!"
    :style="{ width: '28rem', maxWidth: '90vw' }"
    :close-on-escape="false"
    @keydown.escape.prevent="emit('close')"
  >
    <div class="flex items-start gap-4 mb-4">
      <i class="pi pi-exclamation-triangle text-2xl text-red-500 mt-1" />
      <span v-if="user">
        Voulez-vous vraiment supprimer {{ user.username }}? Tous ses fichiers seront supprimés. Cette action est irréversible.
      </span>
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

const props = defineProps(['visible', 'user']);
const emit = defineEmits(['close', 'refreshUserList']);
const loading = ref(false);

async function handleDelete() {
  loading.value = true;
  try {
    await axios.delete(`/api/admin/${props.user.id}/delete`);
  } finally {
    loading.value = false;
  }
  emit('refreshUserList');
  emit('close');
}
</script>
