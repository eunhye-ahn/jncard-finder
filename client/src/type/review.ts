export interface ReviewRequest {
    userId: number,
    storeId: number,
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

export interface MyReivewResponse {
    reviewId: number,
    content: string,
    rating: number,
    reviewDate: string,
    storeName: string
}