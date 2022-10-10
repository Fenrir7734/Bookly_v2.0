import http from '../http-common'
import TokenHelper from "@/utils/TokenHelper";

class AuthenticationService {
    signup(data) {
        return http.post('/auth/register', data)
    }

    signin(data) {
        return http.post('/auth/login', data)
    }

    logout() {
        TokenHelper.removeUser()
    }

    validate() {
        let data = {
            accessToken: JSON.parse(TokenHelper.getUser()).data.accessToken,
            tokenType: "Bearer"
        }
        return http.post('/auth/valid', data)
    }
}

export default new AuthenticationService()