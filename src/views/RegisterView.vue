<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { mdiAccount, mdiAsterisk, mdiEmail, mdiAccountCircle } from '@mdi/js'
import SectionFullScreen from '@/components/SectionFullScreen.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import LayoutGuest from '@/layouts/LayoutGuest.vue'

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const router = useRouter()
const authStore = useAuthStore()
const errorMessage = ref('')
const isLoading = ref(false)

const validateForm = () => {
  if (!form.username) {
    errorMessage.value = 'Please enter a username'
    return false
  }

  if (!form.email) {
    errorMessage.value = 'Please enter an email'
    return false
  }

  if (!form.password) {
    errorMessage.value = 'Please enter a password'
    return false
  }

  if (form.password !== form.confirmPassword) {
    errorMessage.value = 'Passwords do not match'
    return false
  }

  return true
}

const submit = async () => {
  errorMessage.value = ''

  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    await authStore.register({
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      password: form.password
    })

    // Registration successful, redirect to login page
    alert('Registration successful! Please login')
    router.push('/login')
  } catch (error) {
    console.error('Registration failed:', error)
    errorMessage.value = error.message || 'Registration failed, please try again later'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <LayoutGuest>
    <SectionFullScreen v-slot="{ cardClass }" bg="purplePink">
      <CardBox :class="cardClass" is-form @submit.prevent="submit">
        <div class="text-center mb-6">
          <h1 class="text-2xl font-bold">Register New Account</h1>
        </div>

        <div v-if="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {{ errorMessage }}
        </div>

        <FormField label="Username" help="Enter your login username">
          <FormControl v-model="form.username" :icon="mdiAccount" name="username" required />
        </FormField>

        <FormField label="Nickname" help="Optional">
          <FormControl v-model="form.nickname" :icon="mdiAccountCircle" name="nickname" />
        </FormField>

        <FormField label="Email" help="Enter a valid email address">
          <FormControl v-model="form.email" :icon="mdiEmail" type="email" name="email" required />
        </FormField>

        <FormField label="Password" help="Create a password">
          <FormControl v-model="form.password" :icon="mdiAsterisk" type="password" name="password" required />
        </FormField>

        <FormField label="Confirm Password" help="Re-enter your password">
          <FormControl v-model="form.confirmPassword" :icon="mdiAsterisk" type="password" name="confirmPassword"
            required />
        </FormField>

        <template #footer>
          <BaseButtons>
            <BaseButton type="submit" color="info" label="Register" :loading="isLoading" :disabled="isLoading" />
            <BaseButton to="/login" color="info" outline label="Back to Login" />
          </BaseButtons>
        </template>
      </CardBox>
    </SectionFullScreen>
  </LayoutGuest>
</template>
