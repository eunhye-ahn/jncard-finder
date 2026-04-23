import { createReview, getStoreReviews } from "../../axios/api";
import { useQueryClient, useMutation, useQuery, useInfiniteQuery } from "@tanstack/react-query";
import { Dialog, DialogClose, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { useState } from "react";
import type { ReviewCursorRequest, ReviewCursorResponse } from "@/type/review";

type StoreReviewProps = {
    storeId: number
}

export const StoreReview = ({ storeId }: StoreReviewProps) => {
    const [minRating, setMinRating] = useState<number | null>(null)
    const [sort, setSort] = useState("latest")

    const { data,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage, // 두번째부터 요청 로딩중일때만 true
        isLoading, //첫번째 요청 로딩중일때만 true
        isError
    } = useInfiniteQuery({
        //useEffect역할 + 캐시저장소
        queryKey: ["storeReview", storeId, sort, minRating],
        //api 호출
        queryFn: ({ pageParam }: { pageParam: ReviewCursorRequest | null }) => getStoreReviews(Number(storeId), {
            sort,
            minRating,
            cursorId: pageParam?.cursorId ?? null,
            cursorCreatedAt: pageParam?.cursorCreatedAt ?? null,
            cursorRating: pageParam?.cursorRating ?? null
        }).then(res => res.data),
        //queryFn에서 받은 응답 가져오기
        getNextPageParam: (lastPage: ReviewCursorResponse): ReviewCursorRequest | null => {
            if (!lastPage.hasNext) return null;
            return {
                sort,
                minRating,
                cursorId: lastPage.nextCursorId,
                cursorCreatedAt: lastPage.nextCursorCreatedAt,
                cursorRating: lastPage.nextCursorRating,
            }
        },
        //초기커서정보
        initialPageParam: null
    })

    const reviews = data?.pages.flatMap(page => page.reviews) ?? []

    const [rating, setRating] = useState(0)
    const [content, setContent] = useState("")

    const queryClient = useQueryClient();

    const [open, setOpen] = useState(false)

    const { mutate, isPending } = useMutation({
        mutationFn: () => createReview(storeId, { rating, content }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["storeReview"] })
            setOpen(false)
        },
        onError: (error) => {
        }
    })


    return (
        <div>
            <Dialog open={open} onOpenChange={setOpen}>
                <DialogTrigger>
                    <button>리뷰쓰기</button>
                </DialogTrigger>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>리뷰 작성</DialogTitle>
                    </DialogHeader>
                    <div>
                        <div>
                            {[1, 2, 3, 4, 5].map(star => (
                                <span key={star} onClick={() => setRating(star)}>
                                    {star <= rating ? "★" : "☆"}
                                </span>
                            ))}
                        </div>
                        <textarea
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            placeholder="리뷰를 작성해주세요"
                        />
                        <DialogClose asChild>
                            <button onClick={() => mutate()}>
                                {isPending ? "upload..." : "리뷰 등록"}
                            </button>
                        </DialogClose>
                    </div>
                </DialogContent>
            </Dialog>


            <div>리뷰목록</div>
            {isLoading ? <div>로딩중...</div>
                : isError ? <div>리뷰를 불러오는데 실패했습니다</div>
                    : (reviews ?? []).length === 0 ? <div>작성된 리뷰가 없습니다</div>
                        : (reviews ?? []).map(review => (
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