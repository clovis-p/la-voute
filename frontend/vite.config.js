import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import tailwindcss from '@tailwindcss/vite';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  envDir: fileURLToPath(new URL('..', import.meta.url)),
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/login': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/logout': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
