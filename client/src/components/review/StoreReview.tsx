import { useEffect, useState } from "react"
import { getStoreReviews } from "../../axios/api";
import type { StoreReviewResponse } from "../../type/review";

type StoreReviewProps = {
    storeId: number
}

export const StoreReview = ({ storeId }: StoreReviewProps) => {
    const [storeReviews, setStoreReviews] = useState<StoreReviewResponse[]>([]);

    useEffect(() => {
        getStoreReviews(storeId)
            .then((res) => setStoreReviews(res.data))
    }, []);

    return (
        <div>
            {storeReviews.map(review => (
                <div key={review.reviewId}>
                    <p>{review.reviewerName}</p>
                    <p>{review.rating}</p>
                    <p>{review.reviewDate}</p>
                    <p>{review.content}</p>
                </div>
            ))}
        </div>
    )
}