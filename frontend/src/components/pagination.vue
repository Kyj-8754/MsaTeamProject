<template>
  <div class="pagination">
    <button @click="goToPage(1)" :disabled="currentPage === 1">&laquo;</button>
    <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">&lt;</button>

    <button
      v-for="page in pageNumbers"
      :key="page"
      @click="goToPage(page)"
      :class="{ active: page === currentPage }"
    >
      {{ page }}
    </button>

    <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages">&gt;</button>
    <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages">&raquo;</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: Number,
  totalPages: Number,
})

const emit = defineEmits(['changePage'])

const pageNumbers = computed(() => {
  const start = Math.floor((props.currentPage - 1) / 10) * 10 + 1
  const end = Math.min(start + 9, props.totalPages)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

const goToPage = (page) => {
  if (page >= 1 && page <= props.totalPages) {
    emit('changePage', page)
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 1rem 0;
  flex-wrap: wrap;
  gap: 4px;
}

button {
  padding: 6px 10px;
  margin: 0 2px;
  border: 1px solid #ccc;
  background-color: #fff;
  cursor: pointer;
  border-radius: 4px;
}

button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>