import { useEffect, useState } from "react"
import type { StoreDetailResponse } from "../../type/search"
import { getStoreDetail, toggleBookmark } from "../../axios/api";
import { useNavigate } from "react-router-dom";

interface StoreDetailProp {
    selectedStoreId: number,

}

export const StoreDetail = ({ selectedStoreId }: StoreDetailProp) => {
    const [storeInfo, setStoreInfo] = useState<StoreDetailResponse>({} as StoreDetailResponse)
    const navigate = useNavigate();

    useEffect(() => {
        getStoreDetail(selectedStoreId)
            .then((res) => {
                setStoreInfo(res.data)
                console.log(res.data)
            })
    }, [selectedStoreId]);


    const handleBookmarkToggle = () => {
        toggleBookmark(selectedStoreId)
            .then(() => {
                getStoreDetail(selectedStoreId)
                    .then((res) => setStoreInfo(res.data))
            })
    }

    return (
        <div>
            <div>상세보기 화면</div>
            <p onClick={() => navigate(`/${selectedStoreId}`)}>{storeInfo.storeName}</p>
            <p>{storeInfo.category}</p>
            <p>리뷰 {storeInfo.reviewCount}</p>
            <p>{storeInfo.avgRating}</p>
            <p>{storeInfo.bank}</p>
            <p onClick={handleBookmarkToggle}>{storeInfo.bookmarked ? "♥" : "♡"}</p>
            <p>{storeInfo.bookmarkCount}</p>
        </div>
    )
}