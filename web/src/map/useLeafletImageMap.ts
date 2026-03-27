import type { ImageMapConfig } from '@/map/imageMapConfig'
import L from 'leaflet'
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png'
import markerIcon from 'leaflet/dist/images/marker-icon.png'
import markerShadow from 'leaflet/dist/images/marker-shadow.png'
import { onBeforeUnmount, onMounted, ref, type Ref } from 'vue'
import 'leaflet/dist/leaflet.css'

if (import.meta.env.DEV) {
  L.Icon.Default.imagePath = ''
}

L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
})

export function useLeafletImageMap (mapDiv: Ref<HTMLDivElement | null>, imageMapConfig: ImageMapConfig) {
  const map = ref<L.Map | null>(null)

  onMounted(() => {
    if (!mapDiv.value) {
      return
    }

    const mapInstance = L.map(mapDiv.value, {
      crs: L.CRS.Simple,
      zoomControl: false,
      zoomSnap: 0.5,
      zoomDelta: 0.5,
      wheelPxPerZoomLevel: 10,
      maxBoundsViscosity: 0.9,
      scrollWheelZoom: true,
      attributionControl: false,
    })

    const bounds = L.latLngBounds([
      [0, 0],
      [imageMapConfig.height, imageMapConfig.width],
    ])

    const attributionControl = L.control.attribution()
    attributionControl.addAttribution(imageMapConfig.attribution)
    attributionControl.addTo(mapInstance)

    const imageOverlay = L.imageOverlay(imageMapConfig.imageUrl, bounds)
    imageOverlay.addTo(mapInstance)

    mapInstance.setMaxBounds(bounds)
    mapInstance.fitBounds(bounds)

    map.value = mapInstance
  })

  onBeforeUnmount(() => {
    map.value?.remove()
    map.value = null
  })

  return map
}
