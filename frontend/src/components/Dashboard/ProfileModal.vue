<template>
  <Dialog
    v-model:visible="props.visible"
    modal
    header="Mon profil"
    :style="{ width: '25rem', maxWidth: '90vw' }"
    :close-on-escape="false"
    @keydown.escape.prevent="emit('close')"
    @update:visible="!$event && emit('close')"
  >
    <div class="flex flex-col items-center gap-2 mb-4">
      <Avatar :image="avatarSrc" size="xlarge" shape="circle" />
      <FileUpload
        mode="basic"
        :auto="true"
        accept="image/*"
        custom-upload
        choose-icon="pi pi-camera"
        choose-label="Changer la photo"
        :disabled="pictureLoading"
        @uploader="uploadPicture"
      />
      <Message v-if="pictureError" severity="error" size="small" variant="simple">
        {{ pictureError }}
      </Message>
    </div>
    <Form
      :key="formKey"
      v-slot="$form"
      :resolver="resolver"
      :initial-values="initialValues"
      class="flex flex-col gap-4"
      @submit="handleSubmit"
    >
      <div class="flex flex-col gap-1">
        <label for="firstName" class="font-semibold">Prénom</label>
        <InputText
          id="firstName"
          name="firstName"
          type="text"
          class="w-full"
          autocomplete="off"
          :invalid="$form.firstName?.invalid"
        />
        <Message v-if="$form.firstName?.invalid" severity="error" size="small" variant="simple">
          {{ $form.firstName.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <label for="lastName" class="font-semibold">Nom</label>
        <InputText
          id="lastName"
          name="lastName"
          type="text"
          class="w-full"
          autocomplete="off"
          :invalid="$form.lastName?.invalid"
        />
        <Message v-if="$form.lastName?.invalid" severity="error" size="small" variant="simple">
          {{ $form.lastName.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <label class="font-semibold">Changer de mot de passe</label>
        <Password
          name="oldPassword"
          placeholder="Mot de passe actuel"
          :toggle-mask="true"
          :feedback="false"
          input-class="w-full!"
          fluid
          :invalid="$form.oldPassword?.invalid"
        />
        <Message v-if="$form.oldPassword?.invalid" severity="error" size="small" variant="simple">
          {{ $form.oldPassword.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <Password
          name="password"
          placeholder="Nouveau mot de passe"
          :toggle-mask="true"
          :feedback="false"
          input-class="w-full!"
          fluid
          :invalid="$form.password?.invalid"
        />
        <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">
          {{ $form.password.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <Password
          name="confirmPassword"
          placeholder="Confirmer le nouveau mot de passe"
          :toggle-mask="true"
          :feedback="false"
          input-class="w-full!"
          fluid
          :invalid="$form.confirmPassword?.invalid"
        />
        <Message
          v-if="$form.confirmPassword?.invalid"
          severity="error"
          size="small"
          variant="simple"
        >
          {{ $form.confirmPassword.error?.message }}
        </Message>
      </div>
      <Message v-if="serverError" severity="error" size="small" variant="simple">
        {{ serverError }}
      </Message>
      <div class="flex justify-end gap-2">
        <Button type="button" label="Annuler" severity="secondary" @click="emit('close')" />
        <Button type="submit" label="Enregistrer" :loading="loading" />
      </div>
    </Form>
  </Dialog>
</template>

<script setup>
import { Dialog, InputText, Password, Button, Message, Avatar, FileUpload } from 'primevue';
import { Form } from '@primevue/forms';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import axios from 'axios';
import { computed, ref, watch } from 'vue';
import { z } from 'zod';
import { avatarSrc as buildAvatarSrc } from '@/utils/avatar';

const props = defineProps(['visible']);
const emit = defineEmits(['close']);

const serverError = ref('');
const loading = ref(false);
const formKey = ref(0);
const profilePicture = ref(null);
const pictureError = ref('');
const pictureLoading = ref(false);

const avatarSrc = computed(() => buildAvatarSrc(profilePicture.value));
const initialValues = ref({
  firstName: '',
  lastName: '',
  oldPassword: '',
  password: '',
  confirmPassword: '',
});

watch(
  () => props.visible,
  async (isVisible) => {
    if (!isVisible) {
      return;
    }
    serverError.value = '';
    pictureError.value = '';
    try {
      const res = await axios.get('/api/user/obtain-picture');
      profilePicture.value = res.data.profilePicture;
    } catch {
      profilePicture.value = null;
    }
    try {
      const res = await axios.get('/api/user/me');
      initialValues.value = {
        firstName: res.data.firstName,
        lastName: res.data.lastName,
        oldPassword: '',
        password: '',
        confirmPassword: '',
      };
    } catch {
      initialValues.value = {
        firstName: '',
        lastName: '',
        oldPassword: '',
        password: '',
        confirmPassword: '',
      };
    }
    formKey.value++;
  },
);

const schema = z
  .object({
    firstName: z
      .string()
      .min(3, 'Le prénom doit contenir entre 3 et 50 caractères.')
      .max(50, 'Le prénom doit contenir entre 3 et 50 caractères.'),
    lastName: z
      .string()
      .min(3, 'Le nom doit contenir entre 3 et 50 caractères.')
      .max(50, 'Le nom doit contenir entre 3 et 50 caractères.'),
    oldPassword: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
  })
  .superRefine((data, ctx) => {
    if (!data.password) {
      return;
    }
    if (!data.oldPassword) {
      ctx.addIssue({
        path: ['oldPassword'],
        message: "L'ancien mot de passe est requis pour changer de mot de passe.",
      });
    }
    if (data.password.length < 8) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au moins 8 caractères.',
      });
    }
    if (data.password.length > 100) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au maximum 100 caractères.',
      });
    }
    if (!/[A-Z]/.test(data.password)) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au moins une majuscule.',
      });
    }
    if (!/[a-z]/.test(data.password)) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au moins une minuscule.',
      });
    }
    if (!/[0-9]/.test(data.password)) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au moins un chiffre.',
      });
    }
    if (!/[^A-Za-z0-9]/.test(data.password)) {
      ctx.addIssue({
        path: ['password'],
        message: 'Le mot de passe doit contenir au moins un symbole.',
      });
    }
    if (data.password !== data.confirmPassword) {
      ctx.addIssue({
        path: ['confirmPassword'],
        message: 'Les mots de passe ne correspondent pas.',
      });
    }
  });

const resolver = zodResolver(schema);

async function uploadPicture(event) {
  const file = event.files[0];
  if (!file) {
    return;
  }
  pictureError.value = '';
  pictureLoading.value = true;
  try {
    const formData = new FormData();
    formData.append('picture', file);
    const res = await axios.patch('/api/user/update-picture', formData);
    profilePicture.value = res.data.profilePic;
  } catch (err) {
    pictureError.value =
      err.response?.data?.message ?? 'Une erreur est survenue lors de la mise à jour de la photo.';
  } finally {
    pictureLoading.value = false;
  }
}

async function handleSubmit({ valid, values }) {
  if (!valid) {
    return;
  }

  serverError.value = '';
  loading.value = true;
  try {
    const payload = { firstName: values.firstName, lastName: values.lastName };
    if (values.password) {
      payload.oldPassword = values.oldPassword;
      payload.password = values.password;
    }
    await axios.put('/api/user/edit', payload);
    emit('close');
  } catch (err) {
    serverError.value = err.response?.data?.message ?? 'Une erreur est survenue.';
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped></style>
