<template>
  <v-sheet width="100%" class="pt-4 pa-6" rounded="lg">
    <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-2">
      <div>
        <h4 class="text-h6 font-weight-medium">
          Your Stats
        </h4>
        <p class="text-body-2 text-medium-emphasis">
          Track your performance across games
        </p>
      </div>

      <div class="d-flex ga-2">
        <v-btn color="primary" variant="text">
          Log In
        </v-btn>
        <v-btn color="primary" variant="tonal">
          Sign Up
        </v-btn>
      </div>
    </div>

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
  () => `${props.averageGuessTimeSeconds.toFixed(1)} seconds`,
)

const formattedTotalGuessTime = computed(
  () => `${props.totalGuessTimeSeconds} seconds`,
)
</script>

<style scoped>
.blur {
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.1);
}
</style>