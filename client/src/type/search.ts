export interface SearchRequest {
    q: string | null,
    sido: string | null,
    category: string | null,
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