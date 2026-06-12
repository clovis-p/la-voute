<template>
  <DeleteUserModal
    :visible="deleteModalActive"
    :user="userToDelete"
    @refresh-user-list="obtainUsers"
    @close="deleteModalActive = false"
  />
  <div class="flex flex-col flex-1 min-h-0">
    <DataTable
      class="flex-1 min-h-0"
      scrollable
      scroll-height="flex"
      :value="users"
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
    >
      <Column header="" style="width: 3.5rem">
        <template #body="{ data }">
          <Avatar :image="avatarSrc(data.profilePicture)" shape="circle" />
        </template>
      </Column>
      <Column
        field="username"
        header="Nom d'utilisateur"
        :sortable="true"
        :pt="{ bodyCell: { class: 'truncate!' } }"
      />
      <Column
        field="fullName"
        header="Nom"
        :sortable="true"
        :pt="{
          headerCell: { class: 'hidden! md:table-cell!' },
          bodyCell: { class: 'hidden! md:table-cell! truncate!' },
        }"
      />
      <Column header="" class="w-16">
        <template #body="{ data }">
          <Button
            v-if="data.username !== currentUsername"
            type="button"
            outlined
            severity="secondary"
            size="small"
            icon="pi pi-ellipsis-h"
            aria-haspopup="true"
            aria-controls="overlay_menu"
            @click="toggleUserMenu($event, data)"
          />
        </template>
      </Column>
    </DataTable>
    <Menu id="overlay_menu" ref="menu" :model="userMenuItems" :popup="true" />
  </div>
</template>

<script setup>
import { DataTable, Column, Button, Avatar, Menu } from 'primevue';
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';
import DeleteUserModal from '@/components/Dashboard/DeleteUserModal.vue';
import { avatarSrc } from '@/utils/avatar';

const users = ref([]);
const currentUsername = ref(null);
const deleteModalActive = ref(false);
const userToDelete = ref(null);

async function obtainUsers() {
  const res = await axios.get('/api/admin/obtain');
  users.value = res.data.map((user) => ({
    id: user.id,
    username: user.username,
    fullName: [user.firstName, user.lastName].filter(Boolean).join(' '),
    profilePicture: user.profilePicture,
  }));
}

const menu = ref(null);
const activeUser = ref(null);

const userMenuItems = computed(() => {
  if (!activeUser.value) {
    return [];
  }
  return [
    {
      icon: 'pi pi-trash',
      label: 'Supprimer',
      command: () => {
        userToDelete.value = activeUser.value;
        deleteModalActive.value = true;
      },
    },
  ];
});

function toggleUserMenu(event, data) {
  activeUser.value = data;
  menu.value.toggle(event);
}

onMounted(async () => {
  const res = await axios.get('/api/user/me');
  currentUsername.value = res.data.username;
  obtainUsers();
});
</script>
