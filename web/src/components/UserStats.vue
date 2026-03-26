<template>
  <v-sheet class="pt-4 pa-6" rounded="lg" width="100%">
    <div class="d-flex align-center justify-space-between flex-wrap ga-2">
      <div>
        <h4 class="text-h6 font-weight-medium">
          Your Stats
        </h4>
        <p class="text-body-2 text-medium-emphasis">
          Track your performance across games
        </p>
      </div>

      <div v-if="!authenticated" class="d-flex ga-2">
        <v-btn color="primary" variant="text" @click="login">
          Log In
        </v-btn>
        <v-btn color="primary" variant="tonal" @click="signup">
          Sign Up
        </v-btn>
      </div>

      <v-btn v-else color="primary" variant="text" @click="logout">
        Log Out
      </v-btn>
    </div>

    <v-sheet v-if="authenticated" class="blur rounded-lg pl-6 pr-6 pt-4 pb-4 mt-4">
      <dl class="d-flex flex-row ga-8 justify-space-between flex-wrap">
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
    authenticated: boolean
  }>()

  const emits = defineEmits<{
    (e: 'login' | 'signup' | 'logout'): void
  }>()

  const formattedAverageScore = computed(() => props.averageScore.toFixed(1))

  const formattedAverageGuessTime = computed(
    () => `${props.averageGuessTimeSeconds.toFixed(1)} seconds`,
  )

  const formattedTotalGuessTime = computed(
    () => `${props.totalGuessTimeSeconds} seconds`,
  )

  function login () {
    emits('login')
  }

  function signup () {
    emits('signup')
  }

  function logout () {
    emits('logout')
  }
</script>

<style scoped>
.blur {
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.1);
}
</style>
