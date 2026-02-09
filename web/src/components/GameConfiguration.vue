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
            <v-btn-toggle variant="outlined" class="flex-toggle-btn justify-center w-100 mt-4" mandatory divided>
                <v-btn>5</v-btn>
                <v-btn>10</v-btn>
                <v-btn>15</v-btn>
                <v-btn>20</v-btn>
            </v-btn-toggle>

            <p class="text-center text-h6 mt-8">
                Timer
            </p>
            <v-btn-toggle variant="outlined" class="flex-toggle-btn justify-center w-100 mt-4" mandatory divided>
                <v-btn>Off</v-btn>
                <v-btn>30 sec</v-btn>
                <v-btn>1 min</v-btn>
                <v-btn>5 min</v-btn>
                <v-btn>10 min</v-btn>
            </v-btn-toggle>

            <v-spacer/>

            <v-btn color="primary">Start Game</v-btn>
        </v-sheet>
    </v-container>
</template>

<script lang="ts" setup>
  //
</script>

<style scoped>
.flex-toggle-btn :deep(.v-btn) {
  flex: 1 1 0;
}
</style>