import { useEffect, useState } from "react"
import type { MyReivewResponse } from "../../type/review"
import { deleteReview, getMyReviews } from "../../axios/api";

type MyReviewListProps = {
    storeId: number
}

export const MyReviewList = ({ storeId }: MyReviewListProps) => {
    const [myReviewList, setMyReviewList] = useState<MyReivewResponse[]>([]);

    useEffect(() => {
        getMyReviews()
            .then((res) => setMyReviewList(res.data));
    }, []);

    const handleUpdateReview = () => {

    }

    const handleDeleteReview = () => {
        deleteReview(storeId)
            .then(() => console.log("삭제완료"))
    }

    return (
        <div>
            {myReviewList.map(review => (
                <div key={review.reviewId}>
                    <p>{review.storeName}</p>
                    <p>{"★".repeat(review.rating)}{"☆".repeat(5 - review.rating)}</p>
                    <p>{review.reviewDate}</p>
                    <p>{review.content}</p>
                    <button onClick={handleUpdateReview}>수정</button>
                    <button onClick={handleDeleteReview}>삭제</button>
                </div>
            ))}
        </div>
    )
}