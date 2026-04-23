export interface ReviewRequest {
    content: string,
    rating: number
}

export interface StoreReviewResponse {
    reviewId: number,
    content: string,
    rating: number,
    reviewDate: string,
    reviewerName: string
}

export interface ReviewCursorRequest {
    minRating: number | null,
    sort: string,
    cursorId: number | null,
    cursorCreatedAt: string | null,
    cursorRating: number | null
}

export interface ReviewCursorResponse {
    reviews: StoreReviewResponse[],
    nextCursorId: number,
    nextCursorCreatedAt: string,
    nextCursorRating: number,
    hasNext: boolean
}

export interface MyReivewResponse {
    reviewId: number,
    content: string,
    rating: number,
    reviewDate: string,
    storeName: string
}

