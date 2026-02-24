export interface ImageMapConfig {
  imageUrl: string
  width: number
  height: number
  attribution: string
}

export const UMANITOBA_MAP_CONFIG = {
  imageUrl: new URL('@/assets/umanitoba-map.png', import.meta.url).href,
  width: 3850,
  height: 2975,
  attribution: '&copy; University of Manitoba - <a href="https://umanitoba.ca/sites/default/files/2022-08/Self-Guided%20Tour%20-%20Full%20Route%20Map_1.pdf" target="_blank" rel="noopener">campus map</a> (educational use)',
} satisfies ImageMapConfig
