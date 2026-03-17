<template>
  <v-container class="h-100 d-flex flex-column justify-center align-center ga-6" max-width="900">
    <HeroBanner class="rounded-lg"></HeroBanner>
    
    <GameConfiguration @start="startGame" />

    <UserStats
      :total-score="totalScore"
      :total-games="totalGames"
      :total-guess-time-seconds="totalGuessTimeSeconds"
      :total-rounds="totalRounds"
      :average-guess-time-seconds="totalGuessTimeSeconds"
      :average-score="totalScore"
    />
  </v-container>
</template>

<script lang="ts" setup>
  // remove when accounts are fully implemented
  const totalScore=50000
  const totalGames=10
  const totalRounds=50
  const totalGuessTimeSeconds=300

  const router = useRouter()

  async function startGame (totalRounds: number, maxTimerSeconds: number) {
    try {
      const response = await fetch('/api/v1/games', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          totalRounds,
          maxTimerSeconds,
        }),
      })

      if (!response.ok) {
        throw new Error(`Request failed (${response.status} ${response.statusText})`)
      }

      const data = await response.json()

      await router.push({
        path: `/game/${data.gameId}`,
        state: { game: data },
      })
    } catch (caughtError) {
      const errorMessage = caughtError instanceof Error ? caughtError.message : 'Unexpected error'
      console.log(errorMessage)
    }
  }
</script>
