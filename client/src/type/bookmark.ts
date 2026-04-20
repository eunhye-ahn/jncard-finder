export type BookmarkStatus = "ADDED" | "REMOVED";

export interface BookmarkListResponse {
    bookmarkId: number,
    storeName: string,
    category: string,
    address: string
}