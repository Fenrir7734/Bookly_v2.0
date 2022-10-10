<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-md-8 col-sm-12 shadow-lg p-5 bg-light">
          <form @submit.prevent="createBook">
            <div class="row">
              <div class="col-lg-4 text-center">
                <img :src="book.cover ? book.cover : coverPreview" :alt="book.title" @error="coverPreview = 'https://i.imgur.com/bRGfFZk.jpg'" class="img-thumbnail thumbnail " style="cursor: pointer">
              </div>
              <div class="col-lg-8">
                <div class="input-group mb-3">
                  <input type="text" class="form-control" placeholder="Title" v-model="book.title" required min="2" max="30">
                  <div class="invalid-feedback d-block text-center" >
                    {{ errors.title }}
                  </div>
                </div>
                <div class="input-group mb-3">
                  <input type="text" class="form-control" placeholder="Author" v-model="book.author" required min="2" max="60">
                  <div class="invalid-feedback d-block text-center" >
                    {{ errors.author }}
                  </div>
                </div>
                <div class="input-group mb-3">
                  <input type="text" class="form-control" placeholder="Cover" v-model="book.cover">
                  <div class="invalid-feedback d-block text-center" >
                    {{ errors.cover }}
                  </div>
                </div>
                <div class="input-group mb-3">
                  <div class="row vw-100" style="padding-left: 12px; padding-right: 12px">
                    <textarea id="description" rows="11"  v-model="book.description" class="form-control flex-grow-1 w-100" style="min-width: 100%" ></textarea>
                  </div>
                </div>
              </div>
            </div>
            <div class="row mt-3">
              <div class="col-lg-12">
                <button class="btn btn-primary bg-blue float-end border-0" type="submit">Save</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BookService from "@/services/BookService";

export default {
  name: "BookEdit",
  props: ['id', 'title', 'author', 'description', 'cover'],
  data() {
    return {
      book: {
        id: "",
        title: "",
        author: "",
        description: "",
        cover: ""
      },
      errors: {
        id: "",
        title: "",
        author: "",
        description: "",
        cover: ""
      },
      coverPreview: "https://i.imgur.com/bRGfFZk.jpg"
    }
  },
  methods: {
    async createBook() {

      try {
        if (this.book.id) {
          await BookService.update(this.book.id, this.book)
          await this.$router.push({ name: 'success', params: { msg: "Book updated successfully!", curr: `/book-details/${this.book.id}` }})
        } else {
          await BookService.create(this.book)
          await this.$router.push({ name: 'success', params: { msg: "Book created successfully!", curr: `/` }})
        }
        await this.$router.push("/success")
      } catch (error) {
        let status = error.response.status;
        this.globalError = status === 401 || status === 403 || status === 400
            ? error.response.data.message
            : "Unexpected error"
        this.globalError = status === 409 ? "Duplicate ISBN" : this.globalError;
      }
    },
    validate() {
      let valid = true
      if (!this.book.title || this.book.title.length < 2 || this.book.title.length > 30) {
        this.errors.title = "Invalid value"
        valid = false
      } else {
        this.errors.title = ""
      }

      if (!this.book.author || this.book.author.length < 2 || this.book.author.length > 60) {
        this.errors.author = "Invalid value"
        valid = false
      } else {
        this.errors.author = ""
      }

      return valid
    }
  },
  created() {
    this.book.id = this.id ? this.id : ""
    this.book.title = this.title ? this.title : ""
    this.book.author = this.author ? this.author : ""
    this.book.description = this.description ? this.description : ""
    this.book.cover = this.cover ? this.cover : ""
  }
}
</script>

<style scoped>
.thumbnail {
  width: 300px;
  height: 450px;
  overflow: hidden;
  margin: auto;
}
.thumbnail img {
  width: 200px;
  height: 100%;
  object-fit: cover;
}
</style>