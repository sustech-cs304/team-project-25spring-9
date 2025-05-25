<script setup>
import { computed, ref, onMounted } from 'vue'
import { useMainStore } from '@/stores/main'
import {
  mdiChartTimelineVariant,
  mdiImageMultiple,
  mdiArchiveClockOutline,
  mdiAccount,
  mdiTag
} from '@mdi/js'
import * as chartConfig from '@/components/Charts/chart.config.js'
import SectionMain from '@/components/SectionMain.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'

const chartData = ref(null)
const photoCount = ref(0)
const peopleCount = ref(0)
const tagCount = ref(0)

const fillChartData = () => {
  chartData.value = chartConfig.sampleChartData()
}

const fetchCounts = async () => {
  try {
    // Fetch photo count
    const photoResponse = await fetch('http://10.16.60.67:9090/img/getimgcnt?userId=' + mainStore.userId)
    const photoData = await photoResponse.json()
    photoCount.value = photoData.data || 0

    // Fetch people count
    const peopleResponse = await fetch('http://10.16.60.67:9090/img/getpeoplecnt?userId=' + mainStore.userId)
    const peopleData = await peopleResponse.json()
    peopleCount.value = peopleData.data || 0

    // Fetch tag count
    const tagResponse = await fetch('http://10.16.60.67:9090/img/gettagcnt?userId=' + mainStore.userId)
    const tagData = await tagResponse.json()
    tagCount.value = tagData.data || 0
  } catch (error) {
    console.error('Error fetching counts:', error)
  }
}

onMounted(() => {
  fillChartData()
  fetchCounts()
})

const mainStore = useMainStore()

const clientBarItems = computed(() => mainStore.clients.slice(0, 4))

const transactionBarItems = computed(() => mainStore.history)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiChartTimelineVariant" title="Overview" main>
      </SectionTitleLineWithButton>

      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3 mb-6">
        <CardBoxWidget
          color="text-green-500"
          :icon="mdiImageMultiple"
          :number="photoCount"
          label="Photos"
        />
        <CardBoxWidget
          color="text-blue-500"
          :icon="mdiAccount"
          :number="peopleCount"
          label="People"
        />
        <CardBoxWidget
          color="text-purple-500"
          :icon="mdiTag"
          :number="tagCount"
          label="Tags"
        />
      </div>

    </SectionMain>
  </LayoutAuthenticated>
</template>
