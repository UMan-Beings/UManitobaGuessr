<template>
  <v-sheet class="rounded-lg blur pl-6 pr-6 pt-4 pb-4 w-auto">
    <StatItem class="text-right" label="+ Score" :value="score" />
  </v-sheet>
</template>

<script setup lang="ts">
  defineProps<{
    score: number
  }>()
</script>

<style scoped>
.blur{
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.25);
}
</style>
