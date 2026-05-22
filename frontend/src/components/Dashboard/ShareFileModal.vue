<script setup>
import {Dialog, InputGroup, InputText, InputGroupAddon, Button} from "primevue";
import {ref} from "vue";

const props = defineProps(['visible', 'file']);
const emit = defineEmits(['close']);

const shareUrl = "https://lavoute.xyz/file/jk7QJNZW";
const copied = ref(false);

function copyLink() {
  navigator.clipboard.writeText(shareUrl);
  copied.value = true;
  setTimeout(() => copied.value = false, 2000);
}
</script>

<template>
  <Dialog :visible="props.visible" modal :header="`Partager ${props.file?.name}`" :style="{ width: '30rem' }" @update:visible="emit('close')">
    <InputGroup>
      <InputText placeholder="Nom d'utilisateur" />
      <Button icon="pi pi-plus"></Button>
    </InputGroup>
  </Dialog>
</template>
