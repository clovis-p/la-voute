<template>
  <CreateDirectoryModal
    :visible="createDirModalActive"
    :active-dir-id="activeDirId"
    @refresh-file-list="obtainFiles"
    @close="createDirModalActive = false"
  />
  <RenameFileModal
    :visible="renameModalActive"
    :file="fileToRename"
    :active-dir-id="activeDirId"
    @refresh-file-list="obtainFiles"
    @close="renameModalActive = false"
  />
  <ShareFileModal
    :visible="shareModalActive"
    :file="fileToShare"
    @close="shareModalActive = false"
  />
  <DeleteFileModal
    :visible="deleteModalActive"
    :file="fileToDelete"
    @refresh-file-list="obtainFiles"
    @close="deleteModalActive = false"
  />
  <div class="flex flex-col flex-1 min-h-0">
    <div class="flex gap-2 flex-wrap">
      <FileUpload
        v-if="!fileToMove"
        mode="basic"
        :auto="true"
        :multiple="true"
        choose-icon="pi pi-cloud-upload"
        choose-label="Téléverser"
        custom-upload
        @uploader="uploadFile"
      />
      <Button
        v-if="!fileToMove"
        label="Nouveau dossier"
        icon="pi pi-folder-plus"
        severity="secondary"
        @click="createDirModalActive = true"
      />
      <div v-if="uploadProgress != null" class="w-full md:w-80 mt-1.25 md:my-auto md:mx-2">
        <ProgressBar :value="uploadProgress" />
      </div>
      <Button
        v-if="fileToMove"
        :label="`Déplacer ${fileToMove.name} ici`"
        icon="pi pi-arrow-right"
        :loading="movingFile"
        @click="moveFileHere"
      />
      <Button v-if="fileToMove" label="Annuler" severity="secondary" @click="fileToMove = null" />
    </div>
    <Divider class="mt-3! mb-0! z-2!" />
    <DataTable
      class="flex-1 min-h-0"
      scrollable
      scroll-height="flex"
      :value="files"
      row-hover
      :pt="{
        thead: {
          class: 'hidden md:table-header-group',
        },
        tableContainer: {
          class: 'overflow-x-hidden!',
        },
        table: {
          style: 'table-layout: fixed; width: 100%',
        },
        bodyRow: {
          class: 'h-15.25',
        },
      }"
      @row-click="handleTableRowClick"
    >
      <Column header="" style="width: 2.5rem">
        <template #body="{ data }">
          <i class="pi" :class="typeIcons[data.type]" />
        </template>
      </Column>
      <Column
        field="name"
        header="Nom"
        :sortable="true"
        :pt="{ bodyCell: { class: 'truncate!' } }"
      />
      <Column
        field="type"
        sort-field="typeSortKey"
        header="Type"
        :sortable="true"
        :pt="{
          headerCell: { class: 'hidden! md:table-cell!' },
          bodyCell: { class: 'hidden! md:table-cell!' },
        }"
      />
      <Column
        field="size"
        sort-field="sizeBytes"
        header="Taille"
        :sortable="true"
        :pt="{
          headerCell: { class: 'hidden! md:table-cell!' },
          bodyCell: { class: 'hidden! md:table-cell!' },
        }"
      />
      <Column
        field="modifiedAt"
        header="Modifié le"
        :sortable="true"
        :pt="{
          headerCell: { class: 'hidden! md:table-cell!' },
          bodyCell: { class: 'hidden! md:table-cell!' },
        }"
      />
      <Column header="" class="w-16">
        <template #body="{ data }">
          <Button
            v-if="!(data.type === 'Folder' && data.name === '../')"
            type="button"
            outlined
            severity="secondary"
            size="small"
            icon="pi pi-ellipsis-h"
            aria-haspopup="true"
            aria-controls="overlay_menu"
            @click="toggleFileMenu($event, data)"
          />
        </template>
      </Column>
    </DataTable>
    <Menu id="overlay_menu" ref="menu" :model="fileMenuItems" :popup="true" />
  </div>
</template>

<script setup>
import {
  Panel,
  DataTable,
  Column,
  Button,
  Divider,
  FileUpload,
  Menu,
  ConfirmDialog,
  ProgressBar,
} from 'primevue';
import { useConfirm } from 'primevue/useconfirm';
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';
import CreateDirectoryModal from '@/components/Dashboard/CreateDirectoryModal.vue';
import RenameFileModal from '@/components/Dashboard/RenameFileModal.vue';
import ShareFileModal from '@/components/Dashboard/ShareFileModal.vue';
import DeleteFileModal from '@/components/Dashboard/DeleteFileModal.vue';
import { typeIcons, resolveFileType } from '@/utils/fileType.js';

const confirm = useConfirm();

const files = ref([]);
const activeDirId = ref(null);
const createDirModalActive = ref(false);
const renameModalActive = ref(false);
const fileToRename = ref(null);
const fileToMove = ref(null);
const shareModalActive = ref(false);
const fileToShare = ref(null);
const movingFile = ref(false);
const deleteModalActive = ref(false);
const fileToDelete = ref(null);
const uploadProgress = ref(null);

function formatFileSize(bytes) {
  if (bytes < 1024) {
    return bytes + ' o';
  }
  if (bytes < 1024 * 1024) {
    return (bytes / 1024).toFixed(2) + ' Ko';
  }
  if (bytes < 1024 * 1024 * 1024) {
    return (bytes / (1024 * 1024)).toFixed(2) + ' Mo';
  }
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' Go';
}

async function obtainFiles() {
  await axios
    .get('/api/files/obtain' + (activeDirId.value ? '?parentDirId=' + activeDirId.value : ''))
    .then((res) => {
      files.value = [];
      for (const datum of res.data) {
        const fileName = datum.name;

        const fileType = resolveFileType(datum);

        files.value.push({
          id: datum.id,
          downloadId: datum.downloadId,
          name: fileName,
          type: fileType,
          typeSortKey: fileType === 'Folder' ? '0' : '1_' + fileType,
          size: datum.isDirectory ? '' : formatFileSize(datum.size),
          sizeBytes: datum.size,
          modifiedAt: datum.date,
        });
      }
    });
}

const menu = ref(null);
const activeFile = ref(null);

const fileMenuItems = computed(() => {
  if (!activeFile.value) {
    return [];
  }
  const items = [];
  if (activeFile.value.type !== 'Folder') {
    items.push({
      icon: 'pi pi-download',
      label: 'Télécharger',
      command: () => {
        downloadFile(activeFile.value.downloadId);
      },
    });
    items.push({
      icon: 'pi pi-share-alt',
      label: 'Partager',
      command: () => {
        fileToShare.value = activeFile.value;
        shareModalActive.value = true;
      },
    });
  }
  items.push(
    {
      icon: 'pi pi-arrows-h',
      label: 'Déplacer',
      command: () => {
        fileToMove.value = activeFile.value;
      },
    },
    {
      icon: 'pi pi-pencil',
      label: 'Renommer',
      command: () => {
        fileToRename.value = activeFile.value;
        renameModalActive.value = true;
      },
    },
    {
      icon: 'pi pi-trash',
      label: 'Supprimer',
      command: () => {
        fileToDelete.value = activeFile.value;
        deleteModalActive.value = true;
      },
    },
  );
  return items;
});

function toggleFileMenu(event, data) {
  activeFile.value = data;
  menu.value.toggle(event);
}

function downloadFile(fileId) {
  const link = document.createElement('a');
  link.href = `/api/files/${fileId}/download`;
  link.click();
}

onMounted(() => {
  obtainFiles();
});

async function uploadFile(event) {
  let totalBytes = 0;
  for (const file of event.files) {
    totalBytes += file.size;
  }
  const loadedPerFile = new Array(event.files.length).fill(0);
  uploadProgress.value = 0;
  const url = '/api/files/upload' + (activeDirId.value ? '?parentDirId=' + activeDirId.value : '');
  const requests = [];
  for (let i = 0; i < event.files.length; i++) {
    const formData = new FormData();
    formData.append('file', event.files[i]);
    requests.push(
      axios.post(url, formData, {
        onUploadProgress: (progressEvent) => {
          loadedPerFile[i] = progressEvent.loaded;
          let totalLoaded = 0;
          for (const loaded of loadedPerFile) {
            totalLoaded += loaded;
          }
          uploadProgress.value = Math.round((totalLoaded * 100) / totalBytes);
        },
      }),
    );
  }
  await Promise.all(requests);
  uploadProgress.value = null;
  obtainFiles();
}

async function moveFileHere() {
  movingFile.value = true;
  try {
    await axios.patch(`/api/files/${fileToMove.value.id}`, {
      newName: fileToMove.value.name.replace(/\/$/, ''),
      newParentId: activeDirId.value,
    });
  } finally {
    movingFile.value = false;
  }
  fileToMove.value = null;
  obtainFiles();
}

function handleTableRowClick(item) {
  if (item.data.type === 'Folder') {
    activeDirId.value = item.data.id;
    obtainFiles();
  }
}
</script>

<style scoped>
:deep(.p-datatable-tbody > tr) {
  cursor: pointer;
}
</style>
