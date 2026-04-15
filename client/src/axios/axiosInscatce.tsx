import axios from "axios";
import { useAuthStore } from "../store/authStore";
import type { ErrorResponse } from "../type/error";

//create생성 -기본 url, 쿠키 자동설정
export const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        'Content-Type': 'application/json'
    },
    //쿠키설정 나중에
})

//interceptor(res/req)

api.interceptors.request.use(
    (config) => {
        const token = useAuthStore.getState().accessToken
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const errorResponse = error.response?.data as ErrorResponse

        if (errorResponse) {
            alert(errorResponse.message)
        } else {
            alert("네트워크 오류 발생")
        }

        return Promise.reject(error)
    }
)



