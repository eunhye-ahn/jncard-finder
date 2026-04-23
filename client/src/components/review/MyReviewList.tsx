import { deleteReview, getMyReviews } from "../../axios/api";
import { useQuery } from "@tanstack/react-query";


export const MyReviewList = () => {

    const { data, isLoading, isError } = useQuery({
        queryKey: ["myRevies"],
        queryFn: () => getMyReviews().then(res => res.data)
    })

    const handleUpdateReview = () => {

    }

    const handleDeleteReview = (reviewId: number) => {
        deleteReview(reviewId)
            .then(() => console.log("삭제완료"))
    }

    return (
        <div>
            {(data ?? []).map(review => (
                isLoading ? <div>작성한 리뷰가 없습니다</div>
                    : isError ? <div>정보를 불러오는데 오류가 발생했습니다</div>
                        : <div key={review.reviewId}>
                            <p>{review.storeName}</p>
                            <p>{"★".repeat(review.rating)}{"☆".repeat(5 - review.rating)}</p>
                            <p>{review.reviewDate}</p>
                            <p>{review.content}</p>
                            <button onClick={handleUpdateReview}>수정</button>
                            <button onClick={() => handleDeleteReview(review.reviewId)}>삭제</button>
                        </div>
            ))}
        </div>
    )
}