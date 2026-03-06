<template>
  <div class="fill-height d-flex align-center justify-center">
    <GameConfiguration @start="startGame" />
  </div>
</template>

<script lang="ts" setup>
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
