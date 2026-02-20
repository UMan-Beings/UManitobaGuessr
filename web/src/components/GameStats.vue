<template>
  <v-sheet class="blur rounded-lg pl-6 pr-6 pt-4 pb-4">
    <dl class="d-flex flex-row ga-8 text-right">
      <StatItem label="Timer" :value="formattedTime" />
      <StatItem label="Round" :value="formattedRounds" />
      <StatItem label="Score" :value="score" />
    </dl>
  </v-sheet>
</template>

<script lang="ts" setup>
  const props = defineProps<{
    timeSeconds: number
    round: number
    totalRounds: number
    score: number
  }>()

  const formattedTime = computed(() => {
    const minutes = Math.floor(props.timeSeconds / 60)
    const seconds = props.timeSeconds % 60
    return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  })

  const formattedRounds = computed(() => `${props.round}/${props.totalRounds}`)
</script>

<style scoped>
.blur{
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.25);
}
</style>
