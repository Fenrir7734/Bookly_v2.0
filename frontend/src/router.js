import { createWebHistory, createRouter } from 'vue-router'
import SignIn from "@/components/SignIn";
import SignUp from "@/components/SignUp";
import ResponseCard from "@/components/ResponseCard";
import BookList from "@/components/BookList";
import BookDetails from "@/components/BookDetails";
import UserDetails from "@/components/UserDetails";
import CommentEdit from "@/components/CommentEdit";
import BookEdit from "@/components/BookEdit";
import store from "@/store";
import AuthenticationService from "@/services/AuthenticationService";

const routes = [
    {
        path: "/",
        name: "bookList",
        component: BookList
    },
    {
        path: "/book-details/:bookId",
        name: "book-details",
        component: BookDetails
    },
    {
        path: "/signin",
        name: "signin",
        component: SignIn
    },
    {
        path: "/signup",
        name: "signup",
        component: SignUp
    },
    {
        path: "/success",
        name: "success",
        component: ResponseCard,
        props: true
    },
    {
        path: "/user-details/:username",
        name: "user-details",
        component: UserDetails
    },
    {
        path: "/book-details/comment-edit",
        name: "comment-edit",
        component: CommentEdit,
        props: true
    },
    {
        path: "/book-edit",
        name: "book-edit",
        component: BookEdit,
        props: true
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    const publicPages = ['bookList', 'book-details', 'signin', 'signup', 'success', 'user-details']
    const authRequired = !publicPages.includes(to.name)
    const loggedIn = store.state.auth.status.loggedIn

    if (authRequired && !loggedIn) {
        return next('/signin')
    } else if (authRequired) {
        AuthenticationService.validate()
            .then(() => {
                next()
            })
            .catch(() => {
                store.dispatch('auth/logout')
                    .then(() => {
                        next("/signin")
                    })
                    .catch(error => {
                        console.log(error)
                    })
            })
    } else {
        next()
    }
})

export default router;
