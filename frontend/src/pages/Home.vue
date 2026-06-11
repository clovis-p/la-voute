<template>
  <div class="flex flex-col h-screen w-full min-w-0 max-h-100svh">
    <AppBar />
    <div class="px-2 pt-0 pb-2 flex-1 min-w-0 h-0">
      <Panel
        class="mb-2 h-full overflow-hidden flex! flex-col!"
        :pt="{
          header: { class: 'hidden!' },
          contentContainer: { class: 'flex-1! min-h-0! flex! flex-col!' },
          contentWrapper: { class: 'flex-1! min-h-0! flex! flex-col!' },
          content: { class: 'p-3! flex-1! min-h-0! flex! flex-col!' },
        }"
      >
        <UserDashboardView />
      </Panel>
    </div>
  </div>
</template>

<script setup>
import AppBar from '@/components/Dashboard/AppBar.vue';
import UserDashboardView from '@/components/Dashboard/UserDashboardView.vue';
import { Panel } from 'primevue';
</script>
