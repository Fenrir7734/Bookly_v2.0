

<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100 mt-5 mb-5">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-xl-6 col-lg-10 col-sm-12 shadow-lg p-5 bg-light justify-content-center">
          <div class="row">
            <div class="col-lg-4 text-center">
              <img :src="book.cover ? book.cover : 'https://i.imgur.com/bRGfFZk.jpg'" :alt="book.title" @error="book.cover = 'https://i.imgur.com/bRGfFZk.jpg'" class="img-thumbnail thumbnail " style="cursor: pointer">
              <div class="col-12 d-flex justify-content-center mt-4">
                <router-link :to="{ name: 'comment-edit', params: { username: currentUserUsername, bookId: book.id, content: userComment.content, rate: userComment.rate, curr: `/book-details/${this.book.id}`, a: 'POST' }}">
                  <star-rating
                      :increment="1"
                      :rating="userComment && userComment.rate ? userComment.rate : 0"
                      :show-rating="false"
                      @update:rating="setRating"
                      :star-size="30"
                      active-color="#27a2fc"></star-rating>
                </router-link>
              </div>
              <div class="col-12 text-center mt-3" style="color: #6D6E70">
                Rate this book
              </div>
              <div class="col-12 text-center mt-3" style="color: #6D6E70">
                <router-link :to="{ name: 'book-edit', params: { id: book.id, title: book.title, author: book.author, description: book.description, cover: book.cover, curr: `/book-details/${this.book.id}`, a: 'PUT' } }" v-if="isAdmin" class="ps-3" ><i class="bi bi-pencil-square" style="color: #6D6E70"></i></router-link>
                <span class="ps-3" v-if="isAdmin" @click="deleteBook()"><i class="bi bi-trash" style="color: #6D6E70; cursor: pointer"></i></span>
              </div>
            </div>
            <div class="col-lg-8 ps-4">
              <div class="row border-b-grey text-center mt-lg-0 mt-md-5">
                <h2>{{ book.title }}</h2>
              </div>
              <div class="row mt-4">
                <div class="col-lg-4 col-md-12 d-flex justify-content-center">
                  <star-rating
                      :increment="0.01"
                      :rating="stats.rate"
                      :read-only="true"
                      :show-rating="false"
                      :star-size="30"
                      active-color="#27a2fc"></star-rating>
                </div>
                <div class="col-lg-2 col-md-12 text-center mt-lg-0 mt-md-4" style="font-size: large; font-weight: bold; font-family: 'Libre Baskerville', serif;">
                  {{ stats.rate }}
                </div>
                <div class="col-lg-6 col-md-12 text-center mt-lg-0 mt-md-4" style="color: gray">
                  {{ stats.numberOfRates }} ratings &bull; {{ stats.numberOfComments }} reviews
                </div>
              </div>
              <div class="row mt-4">
                {{ book.description }}
              </div>
            </div>
          </div>
          <div class="row border-t-grey mt-4 pt-5">
            <div  v-if="typeof comments !== 'undefined' && comments.length > 0" class="row m-0 justify-content-center">
              <div v-for="comment in comments" :key="comment.id" class="col-md-12 mb-2 mt-2">
                <div v-if="comment.content != null"  >
                  <div class="row mt-4">
                    <div class="col-md-2 col-sm-12 text-center justify-content-center mb-sm-3">
                      <div>
                        <router-link :to='"/user-details/" + comment.user.username' class="link-info" href="#">{{ comment.user.username }}</router-link>
                      </div>
                      <img src="https://freesvg.org/img/abstract-user-flat-1.png" class="avatar img-thumbnail  rounded-circle" style="border: none" />
                      <div class="col-12 d-flex justify-content-center">
                        <star-rating
                            :rating="comment.rate"
                            :read-only="true"
                            :show-rating="false"
                            :star-size="15"
                            active-color="#27a2fc"></star-rating>
                      </div>
                      <span style="color: gray; font-size: small">
                        {{ parseDate(comment.updatedAt) }}
                      </span>
                      <div class="row">
                        <div class="col-12">
                          <router-link :to="{ name: 'comment-edit', params: { bookId: comment.id.bookId, userId: comment.id.userId, content: comment.content, rate: comment.rate, username: comment.user.username, curr: `/book-details/${this.book.id}`, a: 'PUT' } }" v-if="isAdmin || currentUserUsername === comment.user.username" class="ps-3" ><i class="bi bi-pencil-square" style="color: #6D6E70"></i></router-link>
                          <span class="ps-3" v-if="isAdmin || currentUserUsername === comment.user.username" @click="deleteComment(comment.user.username)"><i class="bi bi-trash" style="color: #6D6E70; cursor: pointer"></i></span>
                        </div>
                      </div>
                    </div>
                    <div v-if="comment.content && typeof comment.content !== 'undefined'" class="col-10">
                      {{ comment.content }}
                    </div>
                    <div v-else class="col-10 text-center align-self-center">
                      <h3>No Review</h3>
                    </div>
                  </div>
                </div>
              </div>
              <div class="row d-flex mt-4">
                <el-pagination
                    background
                    layout="prev, pager, next"
                    @current-change="handleCurrentChange"
                    :page-count="page.totalPages"
                    class="pagination justify-content-center">
                </el-pagination>
              </div>
              </div>
            <div v-else class="row text-center pb-5">
              <h2>Comments not found</h2>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BookService from "@/services/BookService";
import ReviewService from "@/services/ReviewService";
import jwt_decode from "jwt-decode";
import router from "@/router";

export default {
  name: "BookDetails",
  data() {
    return {
      book: {},
      stats: {},
      comments: [],
      userComment: {},
      page: {
        size: 10,
        totalElements: 0,
        totalPages: 1,
        number: 0
      },
    }
  },
  methods: {
    retrieveBook() {
      BookService.get(this.$route.params.bookId)
          .then(response => {
            this.book = response.data;
          })
          .catch(error => console.log(error))

      ReviewService.getStats(this.$route.params.bookId)
          .then(response => {
            let stats = response.data;
            if (stats) {
              this.stats["numberOfRates"] = stats.numberOfRates
              this.stats["numberOfComments"] = stats.numberOfComments
              this.stats["rate"] = stats.numberOfRates > 0 ? stats.rate : 0
            }
          })
          .catch(error => console.log(error))
    },
    retrieveComments(page) {
      ReviewService.getByBookId(page, 2, this.$route.params.bookId)
          .then(response => {
            this.comments = response.data.content
                ? response.data.content
                : []
            this.page.size = response.data.size
            this.page.totalElements = response.data.totalElements
            this.page.totalPages = response.data.totalPages
            this.page.number = response.data.number
          })
    },
    retrieveCurrentUser() {
      let user = this.$store.state.auth.user
      if (user) {
        let username = jwt_decode(user.data.accessToken).sub
        ReviewService.getByUsernameAndBookId(username, this.$route.params.bookId)
            .then(response => {
              this.userComment = response.data
            })
            .catch(() => {
              this.userComment = {
                rate: 0,
                content: ""
              }
            })
      }
    },
    deleteBook() {
      BookService.delete(this.book.id)
          .then(() => router.push("/"))
    },
    deleteComment(username) {
      ReviewService.delete(username, this.$route.params.bookId)
          .then(() => this.$router.go())
    },
    parseDate(date) {
      return date ? date.slice(0, 10) : ""
    },
    handleCurrentChange(page) {
      this.retrieveComments(page - 1)
    },
    setRating(rating) {
      this.userComment.rate = rating
    }
  },
  mounted() {
    this.retrieveBook()
    this.retrieveComments(0)
    this.retrieveCurrentUser()
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
.avatar {
  width: 90px;
  height: 90px;
  overflow: hidden;
  margin: auto;
}
</style>