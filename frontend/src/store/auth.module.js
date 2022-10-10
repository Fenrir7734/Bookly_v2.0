import AuthenticationService from "@/services/AuthenticationService";
import TokenHelper from "@/utils/TokenHelper";

const user = JSON.parse(TokenHelper.getUser())
const initialState = user
    ? { status: { loggedIn: true }, user }
    : { status: { loggedIn: false }, user: null };

export const auth = {
    namespaced: true,
    state: initialState,
    actions: {
        login({ commit }, user) {
            return AuthenticationService.signin(user)
                .then(user => {
                    commit('loginSuccess', user);
                    return Promise.resolve(user);
                }).catch(error => {
                    commit('loginFailure');
                    return Promise.reject(error);
                })
        },
        logout({ commit }) {
            AuthenticationService.logout()
            commit('logout')
        },
        register({ commit }, user) {
            return AuthenticationService.signup(user)
                .then(response => {
                    commit('registerSuccess')
                    return Promise.resolve(response.data)
                })
                .catch(error => {
                    commit('registerFailure')
                    return Promise.resolve(error)
                })
        }
    },
    mutations: {
        loginSuccess(state, user) {
            state.status.loggedIn = true;
            state.user = user;
        },
        loginFailure(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        logout(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        registerSuccess(state) {
            state.status.loggedIn = false;
        },
        registerFailure(state) {
            state.status.loggedIn = false;
        }
    }
}