import http from '../http-common'

class UserService {
    getById(id) {
        return http.get(`/users/id/${id}`)
    }

    getByUsername(username) {
        return http.get(`/users/${username}`)
    }
}

export default new UserService()