<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-md-4 col-sm-12 shadow-lg p-5 bg-light">
          <div class="text-center">
            <h3 class="text-primary text-green">{{ text }}</h3>
            <router-link :to="path" class="btn btn-lg px-5 bg-primary text-white mt-4" type="submit">Go Back</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ResponseCard",
  props: ["msg", "curr"],
  data() {
    return {
      text: "SUCCESS",
      path: "/"
    }
  },
  created() {
    this.text = this.msg ? this.msg : this.text
    this.path = this.curr ? this.curr : this.path
  }
}
</script>

<style scoped>

</style>