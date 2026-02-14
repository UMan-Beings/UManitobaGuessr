<template>
  <v-sheet
    class="pa-8 d-flex flex-column"
    elevation="6"
    height="540"
    rounded="xl"
    width="540"
  >
    <h1 class="text-center text-h1 font-weight-bold">
      UMG!
    </h1>
    <h2 class="text-center text-h4">
      UManitobaGuessr
    </h2>

    <v-spacer />

    <h3 class="text-center text-h5">
      Rounds
    </h3>
    <v-btn-toggle
      v-model="rounds"
      class="flex-toggle-btn justify-center w-100 mt-4"
      divided
      mandatory
      variant="outlined"
    >
      <v-btn :value="5">5</v-btn>
      <v-btn :value="10">10</v-btn>
      <v-btn :value="15">15</v-btn>
      <v-btn :value="20">20</v-btn>
    </v-btn-toggle>

    <h3 class="text-center text-h5 mt-8">
      Timer
    </h3>
    <v-btn-toggle
      v-model="timer"
      class="flex-toggle-btn justify-center w-100 mt-4"
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

    <v-spacer />

    <v-btn color="primary" size="x-large">Start Game</v-btn>
  </v-sheet>
</template>

<script lang="ts" setup>
  const rounds = ref<number>(5)
  const timer = ref<number>(0)
</script>

<style scoped>
.flex-toggle-btn :deep(.v-btn) {
  flex: 1 1 0;
}
</style>
