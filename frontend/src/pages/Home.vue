<template>
  <div class="flex flex-col h-screen">
    <AppBar/>
    <UserDashboardView/>
  </div>
</template>

<script setup>
import AppBar from "@/components/Dashboard/AppBar.vue";
import UserDashboardView from "@/components/Dashboard/UserDashboardView.vue";
</script>
