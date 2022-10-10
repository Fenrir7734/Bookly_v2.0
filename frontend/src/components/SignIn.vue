<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-md-4 col-sm-12 shadow-lg p-5 bg-light">
          <div class="text-center">
            <h3 class="text-primary text-green">Sign In</h3>
          </div>
          <form @submit.prevent="handleLogin">
            <div class="p-4">
              <div class="input-group mb-3">
                <span class="input-group-text bg-primary">
                  <i class="bi bi-person-plus-fill text-white"></i></span>
                <input type="text" class="form-control" placeholder="Username" v-model="login.username">
              </div>
              <div class="input-group mb-3">
                <span class="input-group-text bg-primary">
                  <i class="bi bi-key-fill text-white"></i>
                </span>
                <input type="password" class="form-control" placeholder="password" v-model="login.password">
              </div>
              <div class="mt-3 mb-3">
                <div class="invalid-feedback d-block text-center" style="font-size: 16px">
                  {{ error }}
                </div>
              </div>
              <button class="btn btn-primary text-center mt-2" type="submit">
                Login
              </button>
              <p class="text-center mt-5">Don't have an account?
                <router-link to="/signup" class="text-primary">Sign up</router-link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import TokenHelper from "@/utils/TokenHelper";

export default {
  name: "SignIn",
  data() {
    return {
      login: {
        username: "",
        password: ""
      },
      error: ""
    }
  },
  methods: {
    handleLogin() {
      this.$store.dispatch("auth/login", this.login)
          .then(user => {
            TokenHelper.storeUser(user)
            this.$router.push("/")
          })
          .catch(error => {
             this.error = error.response.status === 401
                ? error.response.data.message
                : "Unexpected error";
            console.log(this.error)
          })
    }
  }
}
</script>

<style scoped>

</style>