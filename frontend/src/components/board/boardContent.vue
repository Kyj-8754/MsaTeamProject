<template>
  <div class="post-content" v-html="post.board_content"></div>
</template>

<script setup>
const props = defineProps({
  post: Object
})
</script>

<style scoped>
.post-content {
  padding: 1rem;
  border: 1px solid #ccc;
  min-height: 300px;
  line-height: 1.6;
}
.post-content img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 1rem 0;
}
</style>
