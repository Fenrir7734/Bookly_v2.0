<template>
  <Navbar />
  <router-view :key="$route.fullPath"></router-view>
</template>

<script>
import Navbar from "@/components/Navbar";

export default {
  name: 'App',
  components: {
    Navbar
  }
}
</script>

<style>
#app {
  font-family: Roboto, Helvetica, Arial, sans-serif;
}
</style>
