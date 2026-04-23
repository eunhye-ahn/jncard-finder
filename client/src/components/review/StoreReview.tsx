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


    /**
     * useInfiniteQuery
     */
    const { data,
        fetchNextPage, //다음페이지를 가져오는 함수 :  pageParam으로 queryFn 실행
        hasNextPage,    //getNextPageParam : null 반환 -> false
        isFetchingNextPage, // 두번째부터 요청 로딩중일때만 true
        isLoading, //첫번째 요청 로딩중일때만 true
        isError
    } = useInfiniteQuery({
        //useEffect역할 + 캐시저장소
        queryKey: ["storeReview", storeId, sort, minRating],
        //api 호출
        queryFn: ({ pageParam }: { pageParam: ReviewCursorRequest | null }) => getStoreReviews(storeId, {
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

    /**
     * useInfiniteQuery의 data 구조
     * data.pages = [
     *  { reviews: [...], hasNext: true, ... },
     *  { reviews: [...], hasNext: true, ... },
     *  ...
     * ]
     * => flatMap으로 각 페이지의 reviews를 하나의 배열로 합침
     */
    const reviews = data?.pages.flatMap(page => page.reviews) ?? []

    const [rating, setRating] = useState(0)
    const [content, setContent] = useState("")

    const queryClient = useQueryClient();

    const [open, setOpen] = useState(false)

    /**
     * useMutation
     * 서버 데이터 변경 (delete, update, insert)
     * mutate : mutation 실행시키는 함수 - 버튼클릭같은 이벤트에 연결
     * isPending : 요청 진행 중이면 true
     * onSuccess : 요청성공했을때 콜백
     * onError: 요청 실패했을때 콜백
     * 
     * queryClient.invalidateQueries : 캐시된 데이터가 오래됐다고 표시,
     *                  -> 해당 queryKey의 useQuery가 자동으로 리패치
     *                  -> 새 데이터 화면에 반영
     */
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
            <select
                value={sort ?? "latest"}
                onChange={(e) => setSort(e.target.value)}
            >
                <option value="latest">최신순</option>
                <option value="oldest">오래된순</option>
                <option value="rating">별점순</option>
            </select>
            <button
                onClick={() => setMinRating(prev => prev === 3.5 ? null : 3.5)}>
                별점 3.5이상
            </button>
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
                        ))
            }
            {hasNextPage && (
                <button
                    onClick={() => fetchNextPage()}
                >
                    {isFetchingNextPage ? "로딩중..." : "더보기"}
                </button>
            )}
        </div>
    )
}