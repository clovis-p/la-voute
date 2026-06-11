<template>
  <Dialog
    v-model:visible="props.visible"
    modal
    header="Renommer"
    :style="{ width: '25rem', maxWidth: '90vw' }"
    :close-on-escape="false"
    @keydown.enter.prevent="handleRename"
    @keydown.escape.prevent="emit('close')"
  >
    <div class="flex items-center gap-4 mb-4">
      <label for="rename" class="font-semibold w-24">Nom</label>
      <InputText id="rename" v-model="newName" class="flex-auto" autocomplete="off" autofocus />
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" label="Annuler" severity="secondary" @click="emit('close')" />
      <Button type="button" label="Renommer" :loading="loading" @click="handleRename" />
    </div>
  </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Button, Dialog, InputText } from 'primevue';
import axios from 'axios';

const props = defineProps(['visible', 'file', 'activeDirId']);
const emit = defineEmits(['close', 'refreshFileList']);
const newName = ref('');
const loading = ref(false);

watch(
  () => props.file,
  (file) => {
    if (file) {
      newName.value = file.name.replace(/\/$/, '');
    }
  },
);

async function handleRename() {
  if (newName.value.length > 0 && newName.value !== props.file.name) {
    loading.value = true;
    try {
      await axios.patch(`/api/files/${props.file.id}`, {
        newName: newName.value,
        newParentId: props.activeDirId,
      });
    } finally {
      loading.value = false;
    }
  }
  newName.value = '';
  emit('refreshFileList');
  emit('close');
}
</script>
