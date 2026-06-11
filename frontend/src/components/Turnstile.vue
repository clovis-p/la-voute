<template>
  <div ref="container" class="flex justify-center" />
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';

const SCRIPT_SRC = 'https://challenges.cloudflare.com/turnstile/v0/api.js?render=explicit';

const emit = defineEmits(['verified', 'expired', 'error']);

const container = ref(null);
const siteKey = import.meta.env.VITE_TURNSTILE_SITE_KEY;
let widgetId = null;

function loadScript() {
  if (window.turnstile) {
    return Promise.resolve();
  }
  if (!window.__turnstileScriptPromise) {
    window.__turnstileScriptPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = SCRIPT_SRC;
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed loading Cloudflare Turnstile.'));
      document.head.appendChild(script);
    });
  }
  return window.__turnstileScriptPromise;
}

onMounted(async () => {
  if (!siteKey) {
    emit('error');
    return;
  }
  try {
    await loadScript();
    widgetId = window.turnstile.render(container.value, {
      sitekey: siteKey,
      callback: (token) => emit('verified', token),
      'expired-callback': () => emit('expired'),
      'error-callback': () => emit('error'),
    });
  } catch (err) {
    console.error(err);
    emit('error');
  }
});

onBeforeUnmount(() => {
  if (widgetId !== null && window.turnstile) {
    window.turnstile.remove(widgetId);
  }
});

function reset() {
  if (widgetId !== null && window.turnstile) {
    window.turnstile.reset(widgetId);
  }
}

defineExpose({ reset });
</script>
