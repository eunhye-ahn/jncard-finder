import axios from "axios"
import type { LoginRequest, TokenResponse } from "../type/auth"
import type { BookmarkListResponse, BookmarkStatus } from "../type/bookmark"
import type { MyReivewResponse, ReviewCursorRequest, ReviewCursorResponse, ReviewRequest, StoreReviewResponse } from "../type/review"
import type { SearchRequest, SearchResponse, StoreDetailResponse } from "../type/search"
import type { SignUpRequest } from "../type/user"
import { api } from "./axiosInscatce"

export const searchStore = (params: SearchRequest) => {
    return api.get<SearchResponse>("/search/stores", { params })
}

export const SearchRank = () => {
    return api.get<string[]>("/search/rank")
}

export const SearchAutocomplete = (q: string) => {
    return api.get<string[]>("/search/autocomplete", {
        params: { q }
    })
}

export const Login = (request: LoginRequest) => {
    return api.post<TokenResponse>("/auth/login", request)
}

export const SignUp = (request: SignUpRequest) => {
    return api.post<TokenResponse>("/auth/signup", request)
}

export const Logout = () => {
    return api.post<void>("/auth/logout")
}

export const getStoreDetail = (storeId: number) => {
    return api.get<StoreDetailResponse>(`/search/store/${storeId}`)
}

export const toggleBookmark = (storeId: number) => {
    return api.post<BookmarkStatus>(`/bookmarks/${storeId}`)
}

export const getMyBookmarks = () => {
    return api.get<BookmarkListResponse[]>("/bookmarks")
}

export const createReview = (storeId: number, request: ReviewRequest) => {
    return api.post<MyReivewResponse>(`/reviews/${storeId}`, request)
}

export const deleteReview = (reviewId: number) => {
    return api.delete<void>(`/reviews/${reviewId}`)
}

export const getStoreReviews = (storeId: number, params: ReviewCursorRequest) => {
    return api.get<ReviewCursorResponse>(`/reviews/${storeId}`, { params })
}

export const getMyReviews = () => {
    return api.get<MyReivewResponse[]>("/reviews/my")
}

//새로고침, at만료 시 호출 - 무한루프 방지 순수 axios 사용
export const reissue = () => {
    return axios.post<TokenResponse>("http://localhost:8080/api/auth/reissue", null, {
        withCredentials: true
    });
}