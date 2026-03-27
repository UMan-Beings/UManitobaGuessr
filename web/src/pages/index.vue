<template>
  <v-container class="h-100 d-flex flex-column justify-center align-center ga-6" max-width="900">
    <HeroBanner class="rounded-lg" />

    <GameConfiguration :loading @start="startGame" />

    <UserStats
      :average-guess-time-seconds="averageGuessTimeSeconds"
      :average-score="averageScore"
      :total-games="totalGames"
      :total-guess-time-seconds="totalGuessTimeSeconds"
      :total-rounds="totalRounds"
      :total-score="totalScore"
      :authenticated="jwt !== null"
      @login="login"
      @signup="signup"
      @logout="logout"
    />
  </v-container>
</template>

<script lang="ts" setup>
  const averageGuessTimeSeconds = ref(0)
  const averageScore = ref(0)
  const totalGames = ref(0)
  const totalGuessTimeSeconds = ref(0)
  const totalRounds = ref(0)
  const totalScore = ref(0)

  const router = useRouter()

  const loading = ref(false)
  const jwt = ref<string | null>(localStorage.getItem('jwt'))

  onMounted(() => {
    fetchUserStats()
  })

  function logout () {
    localStorage.removeItem('jwt')
    jwt.value = null
    router.push('/')
  }

  function login () {
    router.push('/login')
  }

  function signup () {
    router.push('/signup')
  }

  async function fetchUserStats () {
    if (!jwt.value) return

    try {
      const response = await fetch('/api/v1/users/me/stats', {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('jwt')}`
        }
      })

      if (!response.ok) {
        throw new Error(`Request failed (${response.status} ${response.statusText})`)
      }

      const data = await response.json()

      averageGuessTimeSeconds.value = data.averageGuessTimeSeconds
      averageScore.value = data.averageScore
      totalGames.value = data.totalGames
      totalGuessTimeSeconds.value = data.totalGuessTimeSeconds
      totalRounds.value = data.totalRounds
      totalScore.value = data.totalScore
    } catch (error) {
      console.error('Error fetching user stats:', error)
    }
  }

  async function startGame (totalRounds: number, maxTimerSeconds: number) {
    try {
      loading.value = true

      const startGameHeaders: Record<string, string> = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      }

      jwt.value = localStorage.getItem('jwt')
      if (jwt.value) {
        startGameHeaders['Authorization'] = `Bearer ${localStorage.getItem('jwt')}`
      }

      const response = await fetch('/api/v1/games', {
        method: 'POST',
        headers: startGameHeaders,
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
    } finally {
      loading.value = false
    }
  }
</script>
