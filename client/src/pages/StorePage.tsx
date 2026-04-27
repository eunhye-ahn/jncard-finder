import { getStoreDetail } from "../axios/api";
import type { StoreDetailResponse } from "../type/search";
import { useParams } from "react-router-dom";
import { StoreReview } from "../components/review/StoreReview";
import { useQuery } from "@tanstack/react-query";
import '@/pages/StorePage.css'

export const StorePage = () => {
    const { storeId } = useParams();

    const { data: storeInfo, isLoading, isError } = useQuery<StoreDetailResponse>({
        queryKey: ["storeDetail", storeId],
        staleTime: 0,   //캐시유지X - 실시간 제어
        refetchOnWindowFocus: true, //탭전환해도 최신화
        queryFn: () => getStoreDetail(Number(storeId)).then(res => res.data),

    })

    if (!storeInfo) return null

    return (
        <div className="store-page">
            {isLoading ? <div className="review-loading">로딩중...</div>
                : <div className="store-card">
                    <p className="store-name">{storeInfo.storeName}</p>
                    <p className="store-category">{storeInfo.category}</p>
                    <div className="store-rating-row">
                        <p className="store-avg-rating">{storeInfo.avgRating}</p>
                        <span className="store-rating-stars">
                            {"★".repeat(Math.round(storeInfo.avgRating))}
                            {"☆".repeat(5 - Math.round(storeInfo.avgRating))}
                        </span>
                        <p className="store-review-count">({storeInfo.reviewCount})</p>
                    </div>
                    <p className="store-address">{storeInfo.address}</p>
                </div>
            }
            <StoreReview storeId={Number(storeId)} />
        </div>
    )
}