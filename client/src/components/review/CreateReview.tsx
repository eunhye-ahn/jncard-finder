import { useState } from "react"
import type { MyReivewResponse, ReviewRequest } from "../../type/review"
import { createReview } from "../../axios/api";
import { useNavigate } from "react-router-dom";

type CreateViewProps = {
    storeId: number
}

export const CreateView = ({ storeId }: CreateViewProps) => {
    const [reviewForm, setReviewForm] = useState<ReviewRequest>({} as ReviewRequest);
    const [data, setData] = useState<MyReivewResponse | null>(null);
    const naviaget = useNavigate();

    const handleCreateReview = () => {
        createReview(storeId, reviewForm)
            .then((res) => {
                setData(res.data);
            })
    }

    return (
        <div>
            <div>
                {[1, 2, 3, 4, 5].map((star) => (
                    <span
                        key={star}
                        onClick={() => setReviewForm((prev) => ({ ...prev, rating: star }))}>
                        {reviewForm.rating >= star ? "★" : "☆"}
                    </span>
                ))}
            </div>
            <div>
                <input type="text"
                    onChange={(e) =>
                        setReviewForm((prev) => ({ ...prev, content: e.target.value }))
                    }
                />
            </div>
            <button onClick={handleCreateReview}>작성 완료</button>
        </div>
    )
}