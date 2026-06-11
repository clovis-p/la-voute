<template>
  <div class="flex min-h-screen items-center justify-center bg-(--p-primary-500) p-4">
    <Card class="w-full max-w-sm">
      <template #content>
        <div v-if="loading" class="flex justify-center py-8">
          <ProgressSpinner />
        </div>
        <div v-else-if="error" class="flex flex-col items-center gap-4 py-4 text-center">
          <i class="pi pi-exclamation-triangle text-5xl text-(--p-red-500)" />
          <p class="text-lg">Ce fichier est introuvable ou inaccessible.</p>
        </div>
        <div v-else class="flex flex-col items-center gap-6 py-4 text-center">
          <i class="pi text-6xl text-(--p-primary-500)" :class="typeIcons[fileType]" />
          <p class="text-xl font-semibold break-all">
            {{ fileName }}
          </p>
          <p class="text-sm text-(--p-text-muted-color)">Partagé par {{ ownerUsername }}</p>
          <Button label="Télécharger" icon="pi pi-download" @click="downloadFile" />
        </div>
      </template>
    </Card>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { Card, Button, ProgressSpinner } from 'primevue';
import { typeIcons, resolveFileType } from '@/utils/fileType.js';

const route = useRoute();
const fileId = route.params.fileId;

const fileName = ref('');
const fileType = ref('Other');
const ownerUsername = ref('');
const loading = ref(true);
const error = ref(false);

onMounted(async () => {
  try {
    const res = await axios.get(`/api/files/${fileId}`);
    if (res.data.isDirectory) {
      error.value = true;
      return;
    }
    fileName.value = res.data.name;
    fileType.value = resolveFileType(res.data);
    ownerUsername.value = res.data.username;
  } catch {
    error.value = true;
  } finally {
    loading.value = false;
  }
});

function downloadFile() {
  const link = document.createElement('a');
  link.href = `/api/files/${fileId}/download`;
  link.click();
}
</script>
