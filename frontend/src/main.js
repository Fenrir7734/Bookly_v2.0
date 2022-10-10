import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import router from "@/router";
import '../public/style.css'
import store from "@/store";
import jwt_decode from "jwt-decode";
import 'mdb-vue-ui-kit/css/mdb.min.css';
import StarRating from 'vue-star-rating'
import VLazyImage from "v-lazy-image";

createApp(App)
    .use(router)
    .use(store)
    .use(VLazyImage)
    .component("star-rating", StarRating)
    .use(ElementPlus)
    .mixin({
        computed: {
            currentUser() {
                return this.$store.state.auth.user
            },
            currentUserRole() {
                return this.currentUser
                    ? jwt_decode(this.currentUser.data.accessToken).roles
                    : ""
            },
            currentUserUsername() {
                return this.currentUser
                    ? jwt_decode(this.currentUser.data.accessToken).sub
                    : ""
            },
            loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            },
            isUser() {
                return this.currentUser
                    ? this.currentUserRole === "ROLE_USER" || this.currentUserRole === "ROLE_ADMIN"
                    : false
            },
            isAdmin() {
                return this.currentUser
                    ? this.currentUserRole === "ROLE_ADMIN"
                    : false
            }
        }
    })
    .mount("#app")
