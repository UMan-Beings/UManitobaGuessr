<template>
  <HomeLinkLogo class="game-overlay position-absolute top-0 left-0 ma-4" />

  <div v-if="loading" class="h-screen w-100 d-flex flex-column align-center justify-center">
    Loading...
  </div>

  <div v-else-if="error !== null" class="h-screen w-100 d-flex flex-column align-center justify-center">
    {{ error }}
  </div>

  <div v-else-if="phase === 'GUESS' || phase === 'REVEAL'">
    <div class="game-overlay position-absolute top-0 right-0 ma-4 d-flex flex-column align-end">
      <GameStats
        :round="round"
        :score="score"
        :time-seconds="formattedTime"
        :total-rounds="totalRounds"
      />
      <GuessResult
        v-if="phase === 'REVEAL'"
        class="mt-2 position-relative bottom-0 right-0"
        :score="scoreReceived"
      />
    </div>

    <div v-if="phase === 'GUESS'">
      <v-img
        class="h-screen w-auto"
        cover
        gradient="rgba(0,0,0,.45)"
        :src="imageUrl"
      >
        <v-img
          class="h-screen w-auto"
          :src="imageUrl"
        />
      </v-img>
      <GuessMap
        class="position-absolute bottom-0 right-0 ma-4"
        @guess="submitGuess"
        @map-clicked="updateGuessCoordinates"
      />
    </div>

    <RevealMap
      v-else-if="phase ==='REVEAL'"
      :actual-lat="actualLat"
      :actual-lng="actualLng"
      :guess-lat="guessLat"
      :guess-lng="guessLng"
      @next="nextRound"
    />
  </div>

  <div v-else-if="phase === 'FINISHED'" class="h-screen w-100 d-flex flex-column align-center justify-center">
    <StatItem class="text-center" label="Score" :value="String(score)" />
  </div>

  <div v-else class="h-screen w-100 d-flex flex-column align-center justify-center">
    Unknown phase ({{ phase }})
  </div>
</template>

<script lang="ts" setup>
  const gameId = useRoute('/game/[gameId]').params.gameId

  const loading = ref(false)
  const error = ref<string | null>(null)

  // common game state refs
  const phase = ref('')
  const round = ref(0)
  const totalRounds = ref(0)
  const score = ref(0)

  let timerIntervalId: number | null = null
  let timeLimitSeconds = 0
  let timer = 0
  const formattedTime = ref(timeLimitSeconds)

  // guess refs
  const imageUrl = ref<string | undefined>(undefined)

  // reveal refs
  const guessLat = ref<number | undefined>(undefined) // corY
  const guessLng = ref<number | undefined>(undefined) // corX
  const actualLat = ref(0)
  const actualLng = ref(0)
  const scoreReceived = ref(0)

  onMounted(async () => {
    const { game, ...otherProperties } = history.state ?? {}

    if (game) {
      updateGameRefs(game)
      history.replaceState(otherProperties, '')
    } else {
      await getGameState()
    }

    timerIntervalId = setInterval(() => {
      if (timerIntervalId !== null && (phase.value === 'FINISHED' || error.value !== null)) {
        clearInterval(timerIntervalId)
      }

      if (!loading.value && phase.value === 'GUESS') {
        timer++
        updateFormattedTime()

        if (timeLimitSeconds > 0 && timer == timeLimitSeconds) {
          if (guessLat.value && guessLng.value) {
            submitGuess(guessLat.value, guessLng.value)
          } else {
            timeout()
          }
        }
      }
    }, 1000)
  })

  onUnmounted(() => {
    if (timerIntervalId !== null) {
      clearInterval(timerIntervalId)
    }
  })

  function updateFormattedTime() {
    formattedTime.value = timeLimitSeconds > 0 ? timeLimitSeconds - timer : timer
  }

  function updateGuessCoordinates (lat?: number, lng?: number) {
    guessLat.value = lat
    guessLng.value = lng
  }

  function updateGameRefs (data: any) {
    phase.value = data.phase
    score.value = data.score
    round.value = data.round
    totalRounds.value = data.totalRounds
    timeLimitSeconds = data.timeLimitSeconds

    if (data.phase === 'GUESS') {
      updateGuessCoordinates()
      imageUrl.value = data.imageUrl
      timer = 0
      updateFormattedTime()
    } else if (data.phase === 'REVEAL') {
      updateGuessCoordinates(data.guessedY, data.guessedX)
      actualLat.value = data.actualY
      actualLng.value = data.actualX
      scoreReceived.value = data.scoreReceived
      timer = data.guessTimeSeconds
    }
  }

  async function callApi (url: string, options: RequestInit = {}) {
    if (loading.value) {
      return
    }

    try {
      loading.value = true
      error.value = null

      const headers: Record<string, string> = {
        'Accept': 'application/json',
        'Content-Type': options.body ? 'application/json' : '',
        ...(options.headers as Record<string, string>),
      }

      const jwt = localStorage.getItem('jwt')
      if (jwt) {
        headers['Authorization'] = `Bearer ${jwt}`
      }

      const response = await fetch(url, { ...options, headers })

      if (!response.ok) {
        throw new Error(`Request failed (${response.status} ${response.statusText})`)
      }

      const data = await response.json()
      updateGameRefs(data)
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Unexpected error'
    } finally {
      loading.value = false
    }
  }

  async function getGameState () {
    await callApi(`/api/v1/games/${gameId}`, { method: 'GET' })
  }

  async function submitGuess (lat: number, lng: number) {
    console.log(`${imageUrl.value}`)
    console.log(`lat/lng (y/x): [${lat}, ${lng}]`)

    await callApi(`/api/v1/games/${gameId}/guess`, {
      method: 'POST',
      body: JSON.stringify({
        corY: lat,
        corX: lng,
        guessTimeSeconds: timer,
      }),
    })
  }

  async function timeout () {
    await callApi(`/api/v1/games/${gameId}/timeout`, { method: 'POST' })
  }

  async function nextRound () {
    await callApi(`/api/v1/games/${gameId}/next`, { method: 'POST' })
  }
</script>

<style scoped>
.game-overlay {
    z-index: 500;
}
</style>
