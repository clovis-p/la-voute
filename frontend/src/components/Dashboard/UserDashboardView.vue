<script setup>
import {Panel, DataTable, Column, Button, Divider, FileUpload, Dialog, InputText} from "primevue";
import axios from 'axios';
import {onMounted, ref} from "vue";
import CreateDirectoryModal from "@/components/Dashboard/CreateDirectoryModal.vue";

const typeIcons = {
  Folder: 'pi-folder',
  Image: 'pi-image',
  Audio: 'pi-headphones',
  Video: 'pi-video',
  Archive: 'pi-box',
  Document: 'pi-file',
  Program: 'pi-code',
  Other: 'pi-question-circle',
};

const files = ref([]);
const activeDirId = ref(null);
const createDirModalActive = ref(false);

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' o';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' Ko';
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(2) + ' Mo';
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' Go';
}

async function obtainFiles() {
  await axios.get("/api/files/obtain" + (activeDirId.value ? "?parentDirId=" + activeDirId.value : "")).then((res) => {
    files.value = [];
    for (const datum of res.data) {
      const fileName = datum.name;

      const fileType = (() => {
        if (datum.isDirectory) return "Folder";
        const ext = datum.name.match(/\.([^.]+)$/)?.[1].toLowerCase() ?? '';
        if (/^(png|jpg|jpeg|gif|webp|svg|bmp|tiff|tif|heic|heif|avif|ico)$/.test(ext)) {
          return "Image";
        }
        if (/^(mp3|wav|flac|aac|ogg|oga|m4a|wma|opus|aiff|aif)$/.test(ext)) {
          return "Audio";
        }
        if (/^(mp4|mkv|webm|mov|avi|wmv|flv|m4v|mpg|mpeg|3gp|ogv|mts|m2ts|vob)$/.test(ext)) {
          return "Video";
        }
        if (/^(zip|tar|gz|tgz|bz2|tbz2|xz|txz|7z|rar|zst|lz|lzma|lzh|cab|iso|ar)$/.test(ext)) {
          return "Archive";
        }
        if (/^(pdf|doc|docx|odt|rtf|txt|md|tex|pages|xls|xlsx|ods|csv|tsv|numbers|ppt|pptx|odp|key|epub|mobi)$/.test(ext)) {
          return "Document";
        }
        if (/^(js|mjs|cjs|jsx|ts|tsx|py|java|c|cc|cpp|cxx|h|hpp|cs|go|rs|rb|php|swift|kt|kts|scala|lua|pl|r|dart|sh|bash|zsh|sql|html|htm|css|scss|sass|less|vue|svelte|json|xml|yaml|yml|toml|ini|exe|msi|dmg|app|deb|rpm|apk|appimage)$/.test(ext)) {
          return "Program";
        }
        return "Other";
      })();

      files.value.push({
        id: datum.id,
        name: fileName,
        type: fileType,
        typeSortKey: fileType === "Folder" ? "0" : "1_" + fileType,
        size: formatFileSize(datum.size),
        sizeBytes: datum.size,
        modifiedAt: datum.createdOn,
      });
    }
  });
}

onMounted(() => {
  obtainFiles();
})

async function uploadFile(event) {
  for (const file of event.files) {
    const formData = new FormData();
    formData.append('file', file);
    await axios.post('/api/files/upload' + (activeDirId.value ? '?parentDirId=' + activeDirId.value : ''), formData);
  }
  obtainFiles();
}

function handleTableRowClick(item) {
  if (item.data.type === "Folder") {
    activeDirId.value = item.data.id;
    obtainFiles();
  }
}
</script>

<template>
  <CreateDirectoryModal :visible="createDirModalActive" :active-dir-id="activeDirId" @refresh-file-list="obtainFiles" @close="createDirModalActive = false" />
  <div class="px-2 pt-0 pb-2 flex-1" >
    <Panel class="mb-2 h-full" :pt="{ header: { class: 'hidden!' }, content: { class: 'p-3!' } }" >
      <div class="flex gap-2">
        <FileUpload mode="basic" :auto="true" :multiple="true" choose-icon="pi pi-cloud-upload" choose-label="Téléverser" custom-upload @uploader="uploadFile"/>
        <Button label="Nouveau dossier" icon="pi pi-folder-plus" severity="secondary" @click="createDirModalActive = true" />
      </div>
      <Divider class="mt-3! mb-0! z-1!" />
      <DataTable :value="files" row-hover @row-click="handleTableRowClick">
        <Column header="" style="width: 2.5rem">
          <template #body="{ data }">
            <i class="pi" :class="typeIcons[data.type]" />
          </template>
        </Column>
        <Column field="name" header="Nom" :sortable="true" />
        <Column field="type" sort-field="typeSortKey" header="Type" :sortable="true" />
        <Column field="size" sort-field="sizeBytes" header="Taille" :sortable="true" />
        <Column field="modifiedAt" header="Modifié le" :sortable="true" />
      </DataTable>
    </Panel>
  </div>
</template>

<style scoped>
:deep(.p-datatable-tbody > tr) {
  cursor: pointer;
}
</style>
