<template>
  <div class="match-description-card">
    <!-- 탭 버튼 -->
    <div class="tab-buttons">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="{ active: activeTab === tab.key }"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 탭별 내용 출력 -->
    <div class="tab-content">
      <div v-if="activeTab === 'description'" v-html="descriptionHtml" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps } from 'vue'

const props = defineProps({
  description: { type: String, default: '' }
})

const activeTab = ref('description')

const tabs = [
  { key: 'description', label: '매치 설명' }
]

// HTML 렌더링용 computed
const descriptionHtml = computed(() => props.description || '<p>매치 설명이 없습니다.</p>')
</script>

<style scoped>
.match-description-card {
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.tab-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tab-buttons button {
  padding: 6px 12px;
  background-color: #eee;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.tab-buttons button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

.tab-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.tab-content img {
  max-width: 100%;
  height: auto;
}
</style>
