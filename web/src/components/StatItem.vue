<template>
  <div class="d-flex flex-column">
    <dt class="text-caption text-medium-emphasis">
      {{ label }}
    </dt>
    <dd class="text-h5 font-weight-bold">
      {{ value }}
    </dd>
  </div>
</template>

<script lang="ts" setup>
  defineProps<{
    label: string
    value: string
  }>()
</script>
