<script setup>
import { computed, ref, onMounted } from 'vue'
import { useMainStore } from '@/stores/main'
import {
  mdiChartTimelineVariant,
  mdiImageMultiple,
  mdiArchiveClockOutline,
} from '@mdi/js'
import * as chartConfig from '@/components/Charts/chart.config.js'
import SectionMain from '@/components/SectionMain.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'

const chartData = ref(null)

const fillChartData = () => {
  chartData.value = chartConfig.sampleChartData()
}

onMounted(() => {
  fillChartData()
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
          :number="114514"
          label="Total Photos"
        />
        <CardBoxWidget
          color="text-blue-500"
          :icon="mdiArchiveClockOutline"
          :number="365"
          label="Days"
        />
        <CardBoxWidget
          color="text-blue-500"
          :icon="mdiArchiveClockOutline"
          :number="365"
          label="Days"
        />
      </div>

    </SectionMain>
  </LayoutAuthenticated>
</template>
