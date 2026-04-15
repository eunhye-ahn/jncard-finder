import type { LoginRequest, TokenResponse } from "../type/auth"
import type { SearchRequest, SearchResponse } from "../type/search"
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
    return api.post("/auth/logout")
}