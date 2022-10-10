<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-md-4 col-sm-12 shadow-lg p-5 bg-light">
          <div class="text-center">
            <h3 class="text-primary text-green">Rating <i>"{{ book.title }}"</i></h3>
          </div>
          <form @submit.prevent="createComment">
            <div v-if="globalError" class="alert alert-danger" role="alert">
              {{ globalError }}
            </div>
            <div class="row">
              <div class="col-12 d-flex justify-content-center">
                <star-rating
                    :v-model="shelf.rate"
                    :rating="shelf.rate"
                    @update:rating="setRating"
                    :show-rating="false"
                    :star-size="30"
                    active-color="#27a2fc"></star-rating>
              </div>
            </div>
            <div class="row mt-5" style="padding-left: 12px; padding-right: 12px">
              <textarea id="description" rows="10" v-model="shelf.content" class="form-control"></textarea>
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
import ReviewService from "@/services/ReviewService";

export default {
  name: "CommentEdit",
  props: ['bookId', 'userId', 'content', 'rate', 'username', 'curr', 'a'],
  data() {
    return {
      book: {},
      shelf: {
        id: {
          bookId: "",
          userId: ""
        },
        content: "",
        rate: 1
      },
      errors: {
        content: "",
        rate: ""
      },
      globalError: ""
    }
  },
  methods: {
    retrieveBook() {
      BookService.get(this.shelf.id.bookId)
          .then(response => {
            this.book = response.data
          })
          .catch(error => console.log(error))
    },
    async createComment() {
      this.globalError = null
      try {
        if (this.action === 'POST') {
          await ReviewService.create(this.username, this.book.id, this.shelf)
          await this.$router.push({ name: 'success', params: { msg: "Comment created successfully!", curr: this.path }})
        } else {
          await ReviewService.update(this.username, this.book.id, this.shelf)
          await this.$router.push({ name: 'success', params: { msg: "Comment updated successfully!", curr: this.path }})
        }
      } catch (error) {
        let status = error.response.status;
        this.globalError = status === 401 || status === 403 || status === 400
            ? error.response.data.message
            : "Unexpected error"
        this.globalError = status === 409 ? "Duplicate ISBN" : this.globalError;
      }
    },
    setRating(rating) {
      this.shelf.rate = rating
    }
  },
  created() {
    console.log(this.username)
    this.shelf.id.bookId = this.bookId ? this.bookId : ""
    this.shelf.id.userId = this.userId ? this.userId : ""
    this.shelf.content = this.content ? this.content : ""
    this.shelf.rate = this.rate ? parseInt(this.rate) : 1
    this.path = this.curr ? this.curr : this.path
    this.action = this.a ? this.a : this.action
  },
  mounted() {
    this.retrieveBook()
  }
}
</script>

<style scoped>

</style>