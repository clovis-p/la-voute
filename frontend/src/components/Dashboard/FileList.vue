<script setup>
import {Panel, DataTable, Column} from "primevue";
import FolderIcon from '@/assets/icons/folder.svg';
import FilmIcon from '@/assets/icons/film.svg';
import ClipboardDocumentListIcon from '@/assets/icons/clipboard-document-list.svg';
import MusicalNoteIcon from '@/assets/icons/musical-note.svg';

const typeIcons = {
  Archive: FolderIcon,
  Image: FilmIcon,
  Document: ClipboardDocumentListIcon,
  Audio: MusicalNoteIcon,
};

const files = [
  { name: 'tp2-jean-guy.zip', type: 'Archive', size: '2.4 Mo', modifiedAt: '2026-04-10' },
  { name: 'photo-jean-guy.jpg', type: 'Image', size: '5.1 Mo', modifiedAt: '2026-04-09' },
  { name: 'matante-labs-inc.pdf', type: 'Document', size: '98.7 Mo', modifiedAt: '2026-03-22' },
  { name: 'matante-labs-revenus-2025.odc', type: 'Document', size: '1.5 Mo', modifiedAt: '2026-04-07' },
];
</script>

<template>
  <div class="px-2 pt-0 pb-2 flex-1" >
    <Panel class="h-full" :pt="{ header: { class: 'hidden!' }, content: { class: 'p-3!' } }" >
      <DataTable :value="files">
        <Column header="" style="width: 2.5rem">
          <template #body="{ data }">
            <component :is="typeIcons[data.type]" class="w-5 h-5" />
          </template>
        </Column>
        <Column field="name" header="Nom" />
        <Column field="type" header="Type" />
        <Column field="size" header="Taille" />
        <Column field="modifiedAt" header="Modifié le" />
      </DataTable>
    </Panel>
  </div>
</template>

<style scoped>

</style>
