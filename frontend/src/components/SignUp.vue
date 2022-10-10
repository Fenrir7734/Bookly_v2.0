<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-md-4 col-sm-12 shadow-lg p-5 bg-light">
          <div class="text-center">
            <h3 class="text-primary text-green">Sign Up</h3>
          </div>
          <form @submit.prevent="registerUser">
            <div class="p-4">
              <div class="input-group mb-3">
                <input type="text" class="form-control" placeholder="First name" v-model="register.firstname">
                <div class="invalid-feedback d-block text-center" >
                  {{ errors.firstname }}
                </div>
              </div>
              <div class="input-group mb-3">
                <input type="text" class="form-control" placeholder="Last name" v-model="register.lastname">
                <div class="invalid-feedback d-block text-center" >
                  {{ errors.lastname }}
                </div>
              </div>
              <div class="input-group mb-3">
                <input type="text" class="form-control" placeholder="Username" v-model="register.username">
                <div class="invalid-feedback d-block text-center" >
                  {{ errors.username }}
                </div>
              </div>
              <div class="input-group mb-3">
                <input type="text" class="form-control" placeholder="Email" v-model="register.email">
                <div class="invalid-feedback d-block text-center" >
                  {{ errors.email }}
                </div>
              </div>
              <div class="input-group mb-3">
                <input type="password" class="form-control" placeholder="Password" v-model="register.password">
                <div class="invalid-feedback d-block text-center" >
                  {{ errors.password }}
                </div>
              </div>
              <button class="btn btn-primary text-center mt-2" type="submit">
                Register
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SignUp",
  data() {
    return {
      register: {
        firstname: "",
        lastname: "",
        username: "",
        password: "",
        email: "",
      },
      error: "",
      errors: {
        firstname: "",
        lastname: "",
        username: "",
        password: "",
        email: "",
      }
    }
  },
  methods: {
    registerUser() {
      if (!this.validate()) {
        return
      }

      this.$store.dispatch('auth/register', this.register)
          .then(response => {
            console.log(response)
            if (!response.response || response.response.status === 201) {
              this.$router.push({ name: "success", params: { msg: "Registered successfully!" } })
            } else {
              let status = response.response.status;
              this.error = status === 401 || status === 403 || status === 409 || status === 400
                  ? response.response.data.message
                  : "Unexpected error"
            }
          })
          .catch(error => {
            console.log(error)
            let status = error.response.status;
            this.error = status === 401 || status === 403 || status === 409 || status === 400
                ? error.response.data.message
                : "Unexpected error"
          })
    },
    validate() {
      let valid = true
      if (!this.register.firstname || this.register.firstname.length < 2 || this.register.firstname.length > 30) {
        this.errors.firstname = "Invalid value"
        valid = false
      } else {
        this.errors.firstname = ""
      }
      if (!this.register.lastname || this.register.lastname.length < 2 || this.register.lastname.length > 30) {
        this.errors.lastname = "Invalid value"
        valid = false
      } else {
        this.errors.lastname = ""
      }
      if (!this.register.username || this.register.lastname.username < 2 || this.register.lastname.username > 30) {
        this.errors.username = "Invalid value"
        valid = false
      } else {
        this.errors.username = ""
      }
      if (!this.register.email || !this.register.email.match(/^\S+@\S+\.\S+$/)) {
        this.errors.email = "Invalid value"
        valid = false
      } else {
        this.errors.email = ""
      }
      if (!this.register.password || this.register.lastname.password < 2 || this.register.lastname.password > 30) {
        this.errors.password = "Invalid value"
        valid = false
      } else {
        this.errors.password = ""
      }
      return valid
    }
  }
}
</script>

<style scoped>

</style>