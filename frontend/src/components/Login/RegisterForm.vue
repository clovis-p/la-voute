<template>
  <div class="flex flex-col gap-8 w-full max-w-sm p-8 md:p-12">
    <div class="flex flex-col items-center gap-4">
      <div class="flex items-center gap-4">
        <AppLogo class="h-20 w-20" />
      </div>
    </div>
    <Form
      v-slot="$form"
      :resolver="resolver"
      :initial-values="initialValues"
      class="flex flex-col gap-6 w-full"
      @submit="handleRegister"
    >
      <div class="flex flex-col gap-1">
        <InputText
          name="firstName"
          type="text"
          placeholder="Prénom"
          class="w-full px-3 py-2 shadow-sm rounded-lg"
          :invalid="$form.firstName?.invalid"
        />
        <Message v-if="$form.firstName?.invalid" severity="error" size="small" variant="simple">
          {{ $form.firstName.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <InputText
          name="lastName"
          type="text"
          placeholder="Nom"
          class="w-full px-3 py-2 shadow-sm rounded-lg"
          :invalid="$form.lastName?.invalid"
        />
        <Message v-if="$form.lastName?.invalid" severity="error" size="small" variant="simple">
          {{ $form.lastName.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <InputText
          name="username"
          type="text"
          placeholder="Nom d'utilisateur"
          class="w-full px-3 py-2 shadow-sm rounded-lg"
          :invalid="$form.username?.invalid"
        />
        <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">
          {{ $form.username.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <Password
          name="password"
          placeholder="Mot de passe"
          :toggle-mask="true"
          :feedback="false"
          input-class="w-full!"
          :invalid="$form.password?.invalid"
        />
        <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">
          {{ $form.password.error?.message }}
        </Message>
      </div>
      <div class="flex flex-col gap-1">
        <Password
          name="confirmPassword"
          placeholder="Confirmer le mot de passe"
          :toggle-mask="true"
          :feedback="false"
          input-class="w-full!"
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
      <Turnstile
        ref="turnstileRef"
        @verified="onTurnstileVerified"
        @expired="onTurnstileReset"
        @error="onTurnstileReset"
      />
      <Message v-if="serverError" severity="error" size="small" variant="simple">
        {{ serverError }}
      </Message>
      <Button
        type="submit"
        label="S'inscrire"
        class="w-full py-2 rounded-lg flex justify-center items-center gap-2"
        :loading="loading"
      >
        <template #icon>
          <i class="pi pi-user text-base! leading-normal!" />
        </template>
      </Button>
    </Form>
    <Button
      label="Retour"
      severity="secondary"
      class="w-full py-2 rounded-lg flex justify-center items-center gap-2"
      @click="emit('switch-to-login')"
    />
  </div>
</template>

<script setup>
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Message from 'primevue/message';
import { Form } from '@primevue/forms';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import AppLogo from '@/assets/AppLogo.vue';
import Turnstile from '@/components/Turnstile.vue';
import axios from 'axios';
import { ref } from 'vue';
import { z } from 'zod';

defineProps(['title', 'subtitle']);
const emit = defineEmits(['switch-to-login']);

const serverError = ref('');
const loading = ref(false);

const turnstileRef = ref(null);
const turnstileToken = ref('');

function onTurnstileVerified(token) {
  turnstileToken.value = token;
}

function onTurnstileReset() {
  turnstileToken.value = '';
}

const initialValues = {
  firstName: '',
  lastName: '',
  username: '',
  password: '',
  confirmPassword: '',
};

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
    username: z
      .string()
      .min(3, "Le nom d'utilisateur doit contenir entre 3 et 50 caractères.")
      .max(50, "Le nom d'utilisateur doit contenir entre 3 et 50 caractères."),
    password: z
      .string()
      .min(8, 'Le mot de passe doit contenir au moins 8 caractères.')
      .max(100, 'Le mot de passe doit contenir au maximum 100 caractères.')
      .regex(/[A-Z]/, 'Le mot de passe doit contenir au moins une majuscule.')
      .regex(/[a-z]/, 'Le mot de passe doit contenir au moins une minuscule.')
      .regex(/[0-9]/, 'Le mot de passe doit contenir au moins un chiffre.')
      .regex(/[^A-Za-z0-9]/, 'Le mot de passe doit contenir au moins un symbole.'),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['confirmPassword'],
  });

const resolver = zodResolver(schema);

async function handleRegister({ valid, values }) {
  if (!valid) {
    return;
  }

  if (!turnstileToken.value) {
    serverError.value = 'Veuillez compléter le captcha';
    return;
  }

  serverError.value = '';
  loading.value = true;
  try {
    await axios.get('/api/csrf');
    await axios.post('/api/user/register', {
      firstName: values.firstName,
      lastName: values.lastName,
      username: values.username,
      password: values.password,
      cfTurnstileResponse: turnstileToken.value,
    });
    emit('switch-to-login');
  } catch (err) {
    serverError.value = err.response?.data?.message ?? 'Une erreur est survenue.';
  } finally {
    loading.value = false;
    turnstileToken.value = '';
    turnstileRef.value?.reset();
  }
}
</script>
