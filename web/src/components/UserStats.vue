<template>
  <v-sheet class="blur rounded-lg pl-6 pr-6 pt-4 pb-4">
    <dl class="d-flex flex-row ga-8 text-right">
      <StatItem label="Total Score" :value="totalScore" />
      <StatItem label="Total Rounds" :value="totalRounds" />
      <StatItem label="Total Games" :value="totalGames" />
      <StatItem label="Average Score" :value="formattedAverageScore" />
      <StatItem label="Average Guess Time" :value="formattedAverageGuessTime" />
      <StatItem label="Total Guess Time" :value="formattedTotalGuessTime" />
    </dl>
  </v-sheet>
</template>

<script lang="ts" setup>
const props = defineProps<{
  totalScore: number
  totalRounds: number
  totalGames: number
  averageScore: number
  averageGuessTimeSeconds: number
  totalGuessTimeSeconds: number
}>()

const formattedAverageScore = computed(() => props.averageScore.toFixed(1))

const formattedAverageGuessTime = computed(
  () => `${props.averageGuessTimeSeconds.toFixed(1)} Seconds`,
)

const formattedTotalGuessTime = computed(
  () => `${props.totalGuessTimeSeconds} Seconds`,
)
</script>

<style scoped>
.blur {
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.4);
}
</style>