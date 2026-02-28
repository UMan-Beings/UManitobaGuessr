<template>
  <div class="container">
    <div class="map-frame overflow-hidden rounded-lg">
      <div ref="mapDiv" class="map-inner cursor-pointer" />
    </div>

    <v-btn
      class="mt-2 w-100 rounded-lg"
      :color="guessMarker ? 'primary' : 'grey-darken-3'"
      :readonly="!guessMarker"
      size="large"
      @click="submitGuess"
    >
      {{ guessMarker ? "Submit guess" : "Place a marker" }}
    </v-btn>
  </div>
</template>

<script lang="ts" setup>
  import L from 'leaflet'
  import { UMANITOBA_MAP_CONFIG } from '@/map/imageMapConfig'
  import { useLeafletImageMap } from '@/map/useLeafletImageMap'

  const emits = defineEmits<{
    (e: 'guess', lat: number, lng: number): void
  }>()

  const mapDiv = ref<HTMLDivElement | null>(null)
  const map = useLeafletImageMap(mapDiv, UMANITOBA_MAP_CONFIG)
  const guessMarker = ref<L.Marker | null>(null)

  watch(map, (mapInstance, _, onCleanup) => {
    if (!mapInstance) return
    
    const minZoom = -1.9
    mapInstance.setMinZoom(minZoom)
    mapInstance.setMaxZoom(0)
    mapInstance.setZoom(minZoom)

    const onMapClick = (e: L.LeafletMouseEvent) => {
      const latLng = e.latlng

      if (guessMarker.value) {
        guessMarker.value.setLatLng(latLng)
      } else {
        const marker = L.marker(latLng)
        mapInstance.addLayer(marker)
        guessMarker.value = marker
      }
    }

    mapInstance.on('click', onMapClick)

    onCleanup(() => {
      mapInstance.off('click', onMapClick)
    })
  })

  function submitGuess () {
    const marker = guessMarker.value
    if (!marker) return

    const latLng = marker.getLatLng()
    emits('guess', latLng.lat, latLng.lng)
  }
</script>

<style scoped>
.container {
  --full-w: 1000;
  --full-h: 773;
  --mini-w-px: 400px;

  width: var(--mini-w-px);
  transition: width .2s ease;
}

.container:hover {
  width: calc(var(--full-w) * 1px);
}

.map-frame {
  aspect-ratio: calc(var(--full-w) / var(--full-h));
}

.map-inner {
  width: calc(var(--full-w) * 1px);
  height: calc(var(--full-h) * 1px);
}
</style>
