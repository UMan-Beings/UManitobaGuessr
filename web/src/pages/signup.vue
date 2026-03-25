<template>
  <HomeLinkLogo class="position-absolute top-0 left-0 ma-4" />
  <v-container class="h-100 d-flex flex-column justify-center align-center" max-width="900">
    <v-sheet class="pa-6" rounded="lg" width="100%">
      <h1 class="text-h5">Sign Up</h1>

      <v-form class="mt-4" @submit.prevent="signup">
        <v-text-field v-model="email" label="Email" required />
        <v-text-field v-model="username" label="Username" required />
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
          Signup successful
        </v-alert>

        <v-btn
          block
          class="mt-4"
          :loading="loading"
          size="large"
          type="submit"
          :color="email && username && password ? 'primary' : 'grey-darken-3'"
          :readonly="email && username && password ? false : true"
        >
          Sign Up
        </v-btn>

        <p class="mt-4 text-center">
          Already have an account? <RouterLink to="/login">Log in</RouterLink>
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

  const username = ref('')
  const email = ref('')
  const password = ref('')

  async function signup () {
    try {
      loading.value = true
      error.value = null
      success.value = false

      const signupData = {
        email: email.value.trim(),
        username: username.value.trim(),
        password: password.value.trim(),
      }

      const response = await fetch('/api/v1/auth/signup', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(signupData),
      })
      
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.message || `Request failed (${response.status} ${response.statusText})`)
      }

      success.value = true
      setTimeout(() => router.push('/login'), 1000)
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Unexpected error'
    } finally {
      loading.value = false
    }
  }
</script>
