<template>
  <Dialog
    v-model:visible="props.visible"
    modal
    header="Créer un dossier"
    :style="{ width: '25rem', maxWidth: '90vw' }"
    :close-on-escape="false"
    @keydown.enter.prevent="handleCreate"
    @keydown.escape.prevent="emit('close')"
  >
    <div class="flex items-center gap-4 mb-4">
      <label for="name" class="font-semibold w-24">Nom</label>
      <InputText id="name" v-model="name" class="flex-auto" autocomplete="off" autofocus />
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" label="Annuler" severity="secondary" @click="emit('close')" />
      <Button type="button" label="Créer" :loading="loading" @click="handleCreate" />
    </div>
  </Dialog>
</template>

<script setup>
import { ref } from 'vue';
import { Button, Dialog, InputText } from 'primevue';
import axios from 'axios';

const props = defineProps(['visible', 'activeDirId']);
const emit = defineEmits(['close', 'refreshFileList']);
const name = ref('');
const loading = ref(false);

async function handleCreate() {
  if (name.value.length > 0) {
    loading.value = true;
    try {
      await axios.post(
        '/api/files/directory?directoryName=' +
          name.value +
          (props.activeDirId ? '&parentDirId=' + props.activeDirId : ''),
      );
    } finally {
      loading.value = false;
    }
  }
  name.value = '';
  emit('refreshFileList');
  emit('close');
}
</script>

<style scoped></style>
