<template>
  <HomeLinkLogo class="game-overlay position-absolute top-0 left-0 ma-4" />

  <div v-if="phase!=='FINISHED'">
    <div class="game-overlay position-absolute top-0 right-0 ma-4 d-flex flex-column align-end">
      <GameStats :round="round" :score="score" :time-seconds="timer" :total-rounds="totalRounds" />
      <GuessResult v-if="phase==='REVEAL'" class="mt-2 position-relative bottom-0 right-0" :score="scoreReceived" />
    </div>

    <RevealMap
      v-if="phase ==='REVEAL'"
      :actual-lat="actualLat"
      :actual-lng="actualLng"
      :guess-lat="guessLat"
      :guess-lng="guessLng"
      @next="nextRound"
    />

    <div v-if="phase==='GUESS'">
      <v-img
        class="h-screen w-auto"
        :src="imageUrl"
      />
      <GuessMap class="position-absolute bottom-0 right-0 ma-4" @guess="submitGuess" />
    </div>
  </div>
  <div v-else class="h-screen w-100 d-flex flex-column align-center justify-center">
    <StatItem class="text-center" label="Score" :value="String(score)" />
  </div>
</template>

<script lang="ts" setup>
  // locationIndex & locations will be removed when the API is ready
  // they are here to demo the UI
  let locationIndex = 0
  const locations = [
    {
      imageUrl: 'https://uman-beings.github.io/UMG-Pictures/crazy-balance-v0-kzeho9rnepre1.jpeg',
      lat: 1340,
      lng: 2282,
    },
    {
      imageUrl: 'https://uman-beings.github.io/UMG-Pictures/deer1.jpeg',
      lat: 833,
      lng: 2705,
    },
    {
      imageUrl: 'https://uman-beings.github.io/UMG-Pictures/IMG_3651.jpg',
      lat: 1480,
      lng: 1770,
    },
    {
      imageUrl: 'https://uman-beings.github.io/UMG-Pictures/IMG_3652.jpg',
      lat: 1172,
      lng: 1776,
    },
    {
      imageUrl: 'https://uman-beings.github.io/UMG-Pictures/IMG_3655.jpg',
      lat: 1344,
      lng: 1209,
    },
  ]

  const timeLimitSeconds = 30
  const timer = ref(timeLimitSeconds)

  const round = ref(locationIndex + 1)
  const totalRounds = ref(locations.length)

  const score = ref(0)
  const scoreReceived = ref(0)

  const imageUrl = ref(locations[locationIndex]?.imageUrl)

  const guessLat = ref<number | undefined>(undefined)
  const guessLng = ref<number | undefined>(undefined)
  const actualLat = ref(1485)
  const actualLng = ref(2130)

  const phase = ref('GUESS')

  let intervalId: number | null = null

  onMounted(() => {
    intervalId = setInterval(() => {
      if (timeLimitSeconds > 0 && timer.value > 0 && phase.value === 'GUESS') {
        timer.value--
      }

      if (timer.value == 0) {
        phase.value = 'REVEAL'
        guessLat.value = undefined
        guessLng.value = undefined
        scoreReceived.value = 0
      }

      if (phase.value === 'FINISHED' && intervalId !== null) {
        clearInterval(intervalId)
      }
    }, 1000)
  })

  onUnmounted(() => {
    if (intervalId !== null) {
      clearInterval(intervalId)
    }
  })

  // will be removed when the API is ready
  function calculateScore (lat1: number, lng1: number, lat2: number, lng2: number) {
    let calculatedScore = 0

    const distance = Math.hypot(
      lat1 - lat2,
      lng1 - lng2,
    )

    const fullScoreDistance = 50
    const maxDistance = 350
    const maxScore = 1000

    if (distance <= fullScoreDistance) {
      calculatedScore = maxScore
    } else {
      const scaledDistance = distance - fullScoreDistance
      const scoringRange = maxDistance - fullScoreDistance

      calculatedScore = Math.max(
        0,
        Math.round(maxScore * (1 - scaledDistance / scoringRange)),
      )
    }

    return calculatedScore
  }

  function submitGuess (lat: number, lng: number) {
    phase.value = 'REVEAL'

    guessLat.value = lat
    guessLng.value = lng
    console.log(`latlng: [${lat}, ${lng}]`)

    actualLat.value = locations[locationIndex]!.lat
    actualLng.value = locations[locationIndex]!.lng

    scoreReceived.value = calculateScore(guessLat.value, guessLng.value, actualLat.value, actualLng.value)
  }

  function nextRound () {
    locationIndex++

    if (locationIndex < locations.length) {
      phase.value = 'GUESS'
      timer.value = timeLimitSeconds
      imageUrl.value = locations[locationIndex]!.imageUrl
      round.value = locationIndex + 1
    } else {
      phase.value = 'FINISHED'
    }

    score.value += scoreReceived.value
  }
</script>

<style scoped>
.game-overlay {
    z-index: 500;
}
</style>
