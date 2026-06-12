<template>
  <div class="p-2">
    <Toolbar>
      <template #start>
        <div class="flex items-center gap-2">
          <Button class="p-0!" text plain @click="router.push('/accueil')">
            <AppLogo class="h-8 w-8" />
          </Button>
        </div>
      </template>

      <template #end>
        <div class="flex items-center gap-2">
          <SplitButton class="user-btn" :model="userMenuItems">
            <span class="flex items-center font-bold">
              <Avatar :image="avatarSrc" />
              <span class="mx-2">{{ username }}</span>
            </span>
          </SplitButton>
        </div>
      </template>
    </Toolbar>
    <ProfileModal :visible="profileModalActive" @close="handleProfileClose" />
  </div>
</template>

<script setup>
import Button from 'primevue/button';
import { Avatar, Toolbar, SplitButton } from 'primevue';
import AppLogo from '@/assets/AppLogo.vue';
import ProfileModal from '@/components/Dashboard/ProfileModal.vue';
import { avatarSrc as buildAvatarSrc } from '@/utils/avatar';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';

const router = useRouter();

const username = ref('');
const profilePicture = ref(null);
const profileModalActive = ref(false);

const avatarSrc = computed(() => buildAvatarSrc(profilePicture.value));

async function fetchPicture() {
  try {
    const res = await axios.get('/api/user/obtain-picture');
    profilePicture.value = res.data.profilePicture;
  } catch {
    profilePicture.value = null;
  }
}

function handleProfileClose() {
  profileModalActive.value = false;
  fetchPicture();
}

onMounted(async () => {
  const res = await axios.get('/api/user/me');
  username.value = res.data.username;
  fetchPicture();
});

const userMenuItems = [
  {
    label: 'Mon profil',
    command: function () {
      profileModalActive.value = true;
    },
  },
  {
    label: 'Se déconnecter',
    command: async function () {
      const response = await axios.post('/logout');
      if (response.status === 200) {
        router.push('/');
      }
    },
  },
];
</script>

<style scoped>
@reference "tailwindcss";

.user-btn :deep(.p-button) {
  @apply p-0;
}
</style>
