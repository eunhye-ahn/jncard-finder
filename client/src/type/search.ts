export interface SearchRequest {
    q: string | null,
    sido: string | null,
    category: string | null,
    bank: string | null,
    cursor: string | null,
    size: number | null,
}

export interface SearchResponse {
    stores: Store[],
    nextCursor: string | null,
    hasNext: boolean
}

export interface Store {
    storeId: number,
    storeName: string,
    sido: string,
    address: string
}

export interface StoreDetailResponse {
    storeId: number,
    storeName: string,
    sido: string,
    address: string,
    category: string,
    bank: string,
    avgRating: number,
    reviewCount: number,
    bookmarkCount: number,
    bookmarked: boolean
}