<template>
  <v-sheet
    class="d-flex flex-column pa-6"
    rounded="lg"
    width="100%"
  >
    <v-sheet
      class="blur px-10 pt-2 pb-6 d-flex flex-column justify-center align-center"
      rounded="lg"
      width="100%"
    >
      <h3 class="text-h5">
        Rounds
      </h3>
      <v-btn-toggle
        v-model="rounds"
        class="flex-toggle-btn w-100 mt-2"
        divided
        mandatory
        variant="outlined"
      >
        <v-btn :value="5">5</v-btn>
        <v-btn :value="10">10</v-btn>
        <v-btn :value="15">15</v-btn>
        <v-btn :value="20">20</v-btn>
      </v-btn-toggle>

      <h3 class="text-h5 mt-6">
        Time Limit
      </h3>
      <v-btn-toggle
        v-model="timer"
        class="flex-toggle-btn w-100 mt-2"
        divided
        mandatory
        variant="outlined"
      >
        <v-btn :value="0">Off</v-btn>
        <v-btn :value="30">30 sec</v-btn>
        <v-btn :value="60">1 min</v-btn>
        <v-btn :value="300">5 min</v-btn>
        <v-btn :value="600">10 min</v-btn>
      </v-btn-toggle>
    </v-sheet>

    <v-btn
      block
      class="mt-6"
      color="primary"
      :loading="loading"
      size="large"
      @click="startGame"
    >
      Start Game
    </v-btn>
  </v-sheet>
</template>

<script lang="ts" setup>
  const rounds = ref(5)
  const timer = ref(0)

  defineProps<{
    loading: boolean
  }>()

  const emits = defineEmits<{
    (e: 'start', totalRounds: number, maxTimerSeconds: number): void
  }>()

  function startGame () {
    emits('start', rounds.value, timer.value)
  }
</script>

<style scoped>
.flex-toggle-btn :deep(.v-btn) {
  flex: 1 1 0;
}
</style>

<style scoped>
.blur {
  backdrop-filter: blur(6px);
  background: rgba(0,0,0,.1);
}
</style>
