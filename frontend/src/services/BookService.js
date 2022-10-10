import http from '../http-common'
import authHeader from "@/services/AuthHeader";

class BookService {
    getAll(page, size) {
        return http.get(`/books?page=${page}&size=${size}`)
    }

    get(id) {
        return http.get(`/books/${id}`)
    }

    create(data) {
        console.log(authHeader())
        return http.post('/books', data, { headers: authHeader() })
    }

    update(id, data) {
        return http.put(`/books/${id}`, data, { headers: authHeader() })
    }

    delete(id) {
        return http.delete(`/books/${id}`, { headers: authHeader() })
    }
}

export default new BookService()