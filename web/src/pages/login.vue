<template>
  <HomeLinkLogo class="position-absolute top-0 left-0 ma-4" />
  <v-container class="h-100 d-flex flex-column justify-center align-center" max-width="900">
    <v-sheet class="pa-6" rounded="lg" width="100%">
      <h1 class="text-h5">Log In</h1>

      <v-form class="mt-4" @submit.prevent="login">
        <v-text-field v-model="email" label="Email" required />
        <v-text-field v-model="password" label="Password" required type="password" />

        <v-alert
          v-if="error"
          class="mt-2"
          type="error"
        >
          {{ error }}
        </v-alert>

        <v-alert
          v-else-if="success"
          class="mt-2"
          type="success"
        >
          Login successful
        </v-alert>

        <v-btn
          block
          class="mt-4"
          :color="email && password ? 'primary' : 'grey-darken-3'"
          :loading="loading"
          :readonly="email && password ? false : true"
          size="large"
          type="submit"
        >
          Log In
        </v-btn>

        <p class="mt-4 text-center">
          Don't have an account? <RouterLink to="/signup">Sign up</RouterLink>
        </p>
      </v-form>
    </v-sheet>
  </v-container>
</template>

<script lang='ts' setup>
  const router = useRouter()

  const loading = ref(false)
  const error = ref<string | null>(null)
  const success = ref(false)

  const email = ref('')
  const password = ref('')

  async function login () {
    try {
      loading.value = true
      error.value = null
      success.value = false

      const loginData = {
        email: email.value.trim(),
        password: password.value.trim(),
      }

      const response = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || `Request failed (${response.status} ${response.statusText})`)
      }

      localStorage.setItem('jwt', data.token)

      success.value = true
      setTimeout(() => router.push('/'), 1000)
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Unexpected error'
    } finally {
      loading.value = false
    }
  }
</script>
