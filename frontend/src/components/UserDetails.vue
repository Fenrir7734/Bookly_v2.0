<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100 mt-5 mb-5">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-xl-6 col-lg-10 col-sm-12 shadow-lg p-5 bg-light justify-content-center">
          <div class="row">
            <div class="col-lg-2 col-md-12 justify-content-center text-center">
              <div>
                <img src="https://freesvg.org/img/abstract-user-flat-1.png" class="avatar img-thumbnail rounded-circle" style="border: none" />
              </div>
              <div>
                {{ user.username }}
              </div>
            </div>
            <div class="col-lg-10 col-md-12">
              <div v-if="typeof reviews !== 'undefined' && reviews.length > 0" >
                <div v-for="review in reviews" :key="review.id.bookId">
                  <div class="row mt-4">
                    <div class="col-md-4 col-sm-12 text-center justify-content-center mb-sm-3">
                      <div class="thumbnail hover-overlay shadow-1-strong">
                        <img :src="review.book.cover ? review.book.cover : 'https://i.imgur.com/bRGfFZk.jpg'" :alt="review.book.title" @error="review.book.cover = 'https://i.imgur.com/bRGfFZk.jpg'" class="img-thumbnail thumbnail " style="cursor: pointer">
                        <router-link :to="'/book-details/' + review.book.id" href="javascript:void(0);">
                          <div class="mask thumbnail mt-0" style="background-color: rgba(251, 251, 251, 0.4)"></div>
                        </router-link>
                      </div>
                      <div class="col-12 d-flex justify-content-center">
                        <star-rating
                            :rating="review.rate"
                            :read-only="true"
                            :show-rating="false"
                            :star-size="15"
                            active-color="#27a2fc"></star-rating>
                      </div>
                      <span style="color: gray; font-size: small">
                        {{ parseDate(review.updatedAt) }}
                      </span>
                      <div class="row">
                        <div class="col-12">
                          <router-link :to="{ name: 'comment-edit', params: { bookId: review.id.bookId, userId: review.id.userId, content: review.content, rate: review.rate, username: user.username, curr: this.$route.path, a: 'PUT' } }" class="ps-3" v-if="isAdmin || currentUserUsername === user.username"><i class="bi bi-pencil-square" style="color: #6D6E70"></i></router-link>
                          <span class="ps-3" v-if="isAdmin || currentUserUsername === user.username" @click="deleteComment(review.id.bookId)"><i class="bi bi-trash" style="color: #6D6E70"></i></span>
                        </div>
                      </div>
                    </div>
                    <div v-if="review.content != null" class="col-lg-8 col-md-12 text-lg-start text-md-start text-sm-center">
                      {{ review.content }}
                    </div>
                    <div v-else class="col-8 text-center align-self-center">
                      <h3>Review not found</h3>
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
              <div v-else class="text-center">
                <h2>Reviews not found</h2>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserService from "@/services/UserService";
import ReviewService from "@/services/ReviewService";
import jwt_decode from "jwt-decode";

export default {
  name: "UserDetails",
  data() {
    return {
      user: {},
      reviews: [],
      page: {
        size: 10,
        totalElements: 0,
        totalPages: 1,
        number: 0
      }
    }
  },
  methods: {
    retrieveUser() {
      UserService.getByUsername(this.$route.params.username)
          .then(response => {
            this.user = response.data
          })
          .catch(error => console.log(error))
    },
    retrieveBook(page) {
      ReviewService.getByUsername(page, 2, this.$route.params.username)
          .then(response => {
            this.reviews = response.data.content
                ? response.data.content
                : []
            this.page.size = response.data.size
            this.page.totalElements = response.data.totalElements
            this.page.totalPages = response.data.totalPages
            this.page.number = response.data.number
          })
          .catch(error => console.log(error))
    },
    parseDate(date) {
      return date ? date.slice(0, 10) : ""
    },
    handleCurrentChange(page) {
      this.retrieveBook(page - 1)
    },
    deleteComment(bookId) {
      let username = jwt_decode(this.$store.state.auth.user.data.accessToken).sub;
      ReviewService.delete(username, bookId)
          .then(() => this.$router.go())
    },
  },
  mounted() {
    this.retrieveUser()
    this.retrieveBook(0)
  }
}
</script>

<style scoped>
.thumbnail {
  width: 150px;
  height: 250px;
  overflow: hidden;
  margin: auto;
}
.thumbnail img {
  width: 150px;
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