<template>
  <MoviesList :title="title"/>
</template>

<script setup lang="ts">
import MoviesList from '../components/MoviesList.vue';

defineProps<{ title: String }>();
</script>
