<template>
  <div class="overflow-hidden position-absolute top-0 h-screen w-100">
    <div ref="mapDiv" class="w-100 h-100" />
  </div>

  <v-btn
    class="map-overlay rounded-lg position-absolute bottom-0 left-0 right-0 ma-4"
    color="primary"
    size="large"
    @click="nextRound"
  >
    Next round
  </v-btn>
</template>

<script lang="ts" setup>
  import L from 'leaflet'
  import { UMANITOBA_MAP_CONFIG } from '@/map/imageMapConfig'
  import { useLeafletImageMap } from '@/map/useLeafletImageMap'

  const props = defineProps<{
    guessLat?: number
    guessLng?: number
    actualLat: number
    actualLng: number
  }>()

  const emits = defineEmits<{
    (e: 'next'): void
  }>()

  const mapDiv = ref<HTMLDivElement | null>(null)
  const map = useLeafletImageMap(mapDiv, UMANITOBA_MAP_CONFIG)

  watch(map, mapInstance => {
    if (!mapInstance) return

    mapInstance.setMinZoom(-1.8)
    mapInstance.setMaxZoom(0)

    const actualLatLng = L.latLng([props.actualLat, props.actualLng])
    const actualMarker = L.marker(actualLatLng)
    mapInstance.addLayer(actualMarker)

    if (props.guessLat != null && props.guessLng != null) {
      const guessLatLng = L.latLng([props.guessLat, props.guessLng])
      const guessMarker = L.marker(guessLatLng)
      mapInstance.addLayer(guessMarker)

      const polyline = L.polyline([guessLatLng, actualLatLng])
      mapInstance.addLayer(polyline)

      mapInstance.flyToBounds(polyline.getBounds(), { padding: [33, 33] })
    } else {
      mapInstance.flyTo(actualMarker.getLatLng())
    }
  })

  function nextRound () {
    emits('next')
  }
</script>

<style scoped>
.map-overlay {
  z-index: 1000;
}
</style>
