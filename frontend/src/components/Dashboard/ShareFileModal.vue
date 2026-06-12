<template>
  <Dialog
    :visible="props.visible"
    modal
    :header="`Partager ${props.file?.name}`"
    :style="{ width: '30rem' }"
    @update:visible="emit('close')"
  >
    <InputGroup class="mb-2">
      <InputText :value="downloadUrl" readonly class="w-[25rem]" />
      <InputGroupAddon class="cursor-pointer" @click="copyLink">
        <i :class="copied ? 'pi pi-check' : 'pi pi-copy'" />
      </InputGroupAddon>
    </InputGroup>
    <InputGroup class="mb-2">
      <p class="mr-2">Public</p>
      <ToggleSwitch v-model="publicShare" />
    </InputGroup>
    <div v-if="!publicShare">
      <InputGroup v-for="(username, index) in usernames" :key="index" class="mb-2">
        <InputText v-model="usernames[index]" placeholder="Nom d'utilisateur" />
        <Button icon="pi pi-minus" severity="danger" @click="handleRemoveUser(index)" />
      </InputGroup>
      <InputGroup class="mb-2">
        <Button
          label="Ajouter un utilisateur"
          icon="pi pi-plus"
          severity="secondary"
          @click="handleAddUser"
        />
      </InputGroup>
      <InputGroup>
        <Button label="Soumettre" @click="handleSubmit" />
      </InputGroup>
    </div>
    <div v-else>
      <InputGroup>
        <Button label="Soumettre" @click="handleSubmit" />
      </InputGroup>
    </div>
  </Dialog>
</template>

<script setup>
import { Dialog, InputGroup, InputText, ToggleSwitch, Button, InputGroupAddon } from 'primevue';
import { computed, ref, watch } from 'vue';
import axios from 'axios';

const props = defineProps(['visible', 'file']);
const emit = defineEmits(['close']);

const downloadUrl = computed(() => `${window.location.origin}/download/${props.file?.downloadId}`);
const copied = ref(false);
const usernames = ref([]);
const publicShare = ref(false);
const loading = ref(false);

watch(
  () => props.visible,
  async (isVisible) => {
    if (!isVisible || !props.file) {
      return;
    }

    loading.value = true;
    try {
      const res = await axios.get(`/api/files/${props.file.id}/visibility`);
      publicShare.value = res.data.visibility === 'public';
      usernames.value = res.data.usernames ?? [];
    } catch {
      publicShare.value = false;
      usernames.value = [];
    } finally {
      loading.value = false;
    }
  },
);

function copyLink() {
  navigator.clipboard.writeText(downloadUrl.value);
  copied.value = true;
  setTimeout(() => (copied.value = false), 2000);
}

function handleAddUser() {
  usernames.value.push('');
}

function handleRemoveUser(index) {
  usernames.value.splice(index, 1);
}

async function handleSubmit() {
  if (publicShare.value) {
    await axios.post(`/api/files/share/${props.file.id}/create`);
  } else {
    const filtered = usernames.value.filter((u) => u.trim() !== '');
    await axios.post(`/api/files/share/${props.file.id}/create`, filtered);
  }
  emit('close');
}
</script>
