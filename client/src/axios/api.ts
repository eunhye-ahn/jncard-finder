import type { SearchRequest, SearchResponse } from "../type/search"
import { api } from "./axiosInscatce"

export const searchStore = (params: SearchRequest) => {
    return api.get<SearchResponse>("/search/stores", { params })
}

export const SearchRank = () => {
    return api.get("/search/rank")
}