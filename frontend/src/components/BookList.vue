<template>
  <div class="container-fluid vertical-center ">
    <div class="row vw-100 mt-5 mb-5">
      <div class="rounded d-flex justify-content-center ">
        <div class="col-xl-6 col-lg-10 col-sm-12 shadow-lg p-5 bg-light justify-content-center">
          <div v-if="typeof books !== 'undefined' && books.length > 0" class="row m-0">
            <div class="col-lg-4 col-md-6 col-12 mb-4 justify-content-center text-center">
              <div class="thumbnail hover-overlay shadow-1-strong">
                <img src="https://i.imgur.com/qHJPwwu.png" class="img-thumbnail thumbnail" style="cursor: pointer"/>
                <router-link :to="{ name: 'book-edit' }" href="javascript:void(0);">
                  <div class="mask thumbnail" style="background-color: rgba(251, 251, 251, 0.4)"></div>
                </router-link>
              </div>
            </div>
            <div v-for="book in books" :key="book.id" class="col-lg-4 col-md-6 col-12 mb-4 justify-content-center text-center">
              <div class="thumbnail hover-overlay shadow-1-strong">
                <img :src="book.cover ? book.cover : 'https://i.imgur.com/bRGfFZk.jpg'" :alt="book.title" @error="book.cover = 'https://i.imgur.com/bRGfFZk.jpg'" class="img-thumbnail thumbnail " style="cursor: pointer" >
                <router-link :to="'/book-details/' + book.id" href="javascript:void(0);" >
                  <div class="mask thumbnail" style="background-color: rgba(251, 251, 251, 0.4)" data-bs-toggle="popover" data-bs-trigger="hover focus" :title="book.title" ></div>
                </router-link>
              </div>
            </div>
          </div>
          <div v-else class="row d-flex text-center mb-5 mt-5">
            <h2>Books not found</h2>
          </div>
          <div class="row d-flex ">
            <el-pagination
                background
                layout="prev, pager, next"
                @current-change="handleCurrentChange"
                :page-count="page.totalPages"
                class="pagination justify-content-center">
            </el-pagination>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BookService from "@/services/BookService";
import ReviewService from "@/services/ReviewService";

export default {
  name: "BookList",
  data() {
    return {
      books: [],
      booksStats: [],
      page: {
        size: 10,
        totalElements: 0,
        totalPages: 1,
        number: 0
      }
    }
  },
  methods: {
    retrieveBooks(page) {
      BookService.getAll(page, 8, this.$route.params.genre)
          .then(response => {
            this.books = response.data
                ? response.data.content
                : []
            this.page.size = response.data.size
            this.page.totalElements = response.data.totalElements
            this.page.totalPages = response.data.totalPages
            this.page.number = response.data.number
            for (let i = 0; i < this.books.length; i++) {
              ReviewService.getStats(this.books[i].id)
                  .then(response => {
                    let stats = response.data;
                    this.books[i]["numberOfRates"] = stats.numberOfRates
                    this.books[i]["numberOfComments"] = stats.numberOfComments
                    this.books[i]["rate"] = stats.numberOfRates > 0 ? stats.rate : "-"
                  })
                  .catch(error => console.log(error))
            }
          })
          .catch(error => console.log(error))
    },
    handleCurrentChange(page) {
      this.retrieveBooks(page - 1)
    }
  },
  mounted() {
    this.retrieveBooks(0)
  }
}
</script>

<style scoped>
.thumbnail
{
  width: 200px;
  height: 300px;
  overflow: hidden;
  margin: auto;
}
.thumbnail img {
  width: 200px;
  height: 100%;
  object-fit: cover;
}

@media only screen and (max-width: 1450px){
  .thumbnail
  {
    width: 150px;
    height: 225px;
    overflow: hidden;
    margin: auto;
  }
}

@media screen and (max-width: 1200px) {
  .thumbnail
  {
    width: 200px;
    height: 300px;
    overflow: hidden;
    margin: auto;
  }
}
</style>