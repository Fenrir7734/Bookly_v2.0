<template>
  <nav class="navbar navbar-expand-lg navbar-light navbar-green navbar-white" style="background-color: white">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand brand"><span class="text-primary">Book</span><span style="color: #6D6E70; font-weight: 200">Tool</span></router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarScroll" aria-controls="navbarScroll" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse w-100 order-3" id="navbarScroll">
        <ul class="navbar-nav ml-auto">
          <li v-if="!loggedIn" class="d-flex nav-item">
            <router-link to="/signin" class="nav-link nav-text-color">Sign in</router-link>
          </li>
          <li v-if="!loggedIn" class="d-flex nav-item">
            <router-link to="/signup" class="nav-link nav-text-color" href="#">Sing Up</router-link>
          </li>
          <li v-if="loggedIn" class="d-flex nav-item">
            <router-link :to='"/user-details/" + currentUserUsername' class="nav-link nav-text-color">Hi <b>{{ currentUserUsername }}</b>!</router-link>
          </li>
          <li v-if="loggedIn" class="d-flex nav-item" style="cursor: pointer">
            <a class="nav-link" @click="logout">Log out</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: "NavBar",
  methods: {
    logout() {
      this.$store.dispatch('auth/logout')
          .then(() => {
            this.$router.push("/")
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
}
</script>

<style scoped>
.navbar-white {
  background-color: white;
  border-bottom: 1px solid lightgray;
}
.brand {
  font-size: 24px;
  font-weight: bold;
}
</style>