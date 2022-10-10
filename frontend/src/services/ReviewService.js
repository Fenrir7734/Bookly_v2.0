import http from '../http-common'
import authHeader from "@/services/AuthHeader";

class ReviewService {
    getByUsernameAndBookId(username, bookId) {
        return http.get(`/reviews/${username}/${bookId}`)
    }

    getByBookId(page, size, bookId) {
        return http.get(`/reviews/book/${bookId}?page=${page}&size=${size}`)
    }

    getByUsername(page, size, username) {
        return http.get(`/reviews/user/${username}?page=${page}&size=${size}`)
    }

    getStats(bookId) {
        return http.get(`/reviews/book/${bookId}/stats`)
    }

    create(username, bookId, data) {
        return http.post(`/reviews/${username}/${bookId}`, data, { headers: authHeader() })
    }

    update(username, bookId, data) {
        return http.put(`/reviews/${username}/${bookId}`, data, { headers: authHeader() })
    }

    delete(username, bookId) {
        return http.delete(`/reviews/${username}/${bookId}`, { headers: authHeader() })
    }
}

export default new ReviewService()