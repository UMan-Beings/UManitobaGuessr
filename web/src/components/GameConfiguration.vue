<template>
    <v-container class="fill-height d-flex align-center justify-center">
        <v-sheet height="520" width="520" class="pa-8 d-flex flex-column" rounded="xl" elevation="6">
            <p class="text-center text-h1 font-weight-bold">
                UMG!
            </p>
            <p class="text-center text-h4">
                UManitobaGuessr
            </p>

            <v-spacer />

            <p class="text-center text-h6">
                Rounds
            </p>
            <v-btn-toggle v-model="rounds" variant="outlined" class="flex-toggle-btn justify-center w-100 mt-4" mandatory divided>
                <v-btn :value="5">5</v-btn>
                <v-btn :value="10">10</v-btn>
                <v-btn :value="15">15</v-btn>
                <v-btn :value="20">20</v-btn>
            </v-btn-toggle>

            <p class="text-center text-h6 mt-8">
                Timer
            </p>
            <v-btn-toggle v-model="timer" variant="outlined" class="flex-toggle-btn justify-center w-100 mt-4" mandatory divided>
                <v-btn :value="0">Off</v-btn>
                <v-btn :value="30">30 sec</v-btn>
                <v-btn :value="60">1 min</v-btn>
                <v-btn :value="300">5 min</v-btn>
                <v-btn :value="600">10 min</v-btn>
            </v-btn-toggle>

            <v-spacer/>

            <v-btn color="primary">Start Game</v-btn>
        </v-sheet>
    </v-container>
</template>

<script lang="ts" setup>
const rounds = ref<number>(5);
const timer = ref<number>(0);
</script>

<style scoped>
.flex-toggle-btn :deep(.v-btn) {
  flex: 1 1 0;
}
</style>