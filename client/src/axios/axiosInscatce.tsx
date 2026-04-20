import axios from "axios";
import { useAuthStore } from "../store/authStore";
import type { ErrorResponse } from "../type/error";
import { reissue } from "./api";

//create생성 -기본 url, 쿠키 자동설정
export const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
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

    async (error) => {
        if (error.response.status === 401 && !error.config._retry) {
            error.config._retry = true; //reissue 실패시, 무환순환 방지
            try {
                const res = await reissue();
                //훅은 리액트 컴포넌트 또는 커스텀 훅에서만 호출 가능
                useAuthStore.getState().setAccessToken(res.data.accessToken);
                return api(error.config)
            } catch (e) {
                useAuthStore.getState().setAccessToken(null);
                window.location.href = "/login";
                return Promise.reject(e);
            }
        }

        const errorResponse = error.response?.data as ErrorResponse
        if (errorResponse) {
            alert(errorResponse.message)
        } else {
            alert("네트워크 오류 발생")
        }
        return Promise.reject(error)
    }
)



