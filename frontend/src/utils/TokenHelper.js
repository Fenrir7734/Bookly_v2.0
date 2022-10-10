import jwt_decode from "jwt-decode";

class TokenHelper {
    TOKEN_NAME = "BS-user"

    storeUser(user) {
        localStorage.setItem(this.TOKEN_NAME, JSON.stringify(user))
    }

    getUser() {
        return localStorage.getItem(this.TOKEN_NAME)
    }

    removeUser() {
        localStorage.removeItem(this.TOKEN_NAME)
    }

    isUser() {
        let role = this.#getUserRole()
        return role === 'ROLE_USER'
            || role === 'ROLE_ADMIN'
    }

    isAdmin() {
        let role = this.#getUserRole()
        return role === 'ROLE_ADMIN'
    }

    #getUserRole() {
        if (localStorage.getItem(this.TOKEN_NAME)) {
            let user = JSON.parse(localStorage.getItem(this.TOKEN_NAME))
            let decodedToken = jwt_decode(user.data.accessToken);
            return decodedToken.roles
        }
        return ""
    }
}

export default new TokenHelper();