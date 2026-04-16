import { useEffect, useState } from "react"
import type { Store, StoreDetailResponse } from "../../type/search"
import { getStoreDetail, toggleBookmark } from "../../axios/api";

interface StoreDetailProp {
    selectedStoreId: number,

}

export const StoreDetail = ({ selectedStoreId }: StoreDetailProp) => {
    const [storeInfo, setStoreInfo] = useState<StoreDetailResponse>()
    const [isBookmarked, setIsBookmarked] = useState(false);

    useEffect(() => {
        getStoreDetail(selectedStoreId)
            .then((res) => {
                setStoreInfo(res.data)
            })
    }, [selectedStoreId, isBookmarked]);

    const handleTest = () => {
        toggleBookmark(selectedStoreId)
            .then((res) => {
                if (res.data == "ADDED")
                    setIsBookmarked(true)
                if (res.data == "REMOVED")
                    setIsBookmarked(false)
            })
    }

    return (
        <div>
            <div>상세보기 화면</div>
            <p>{storeInfo?.storeName}</p>
            <p>{storeInfo?.category}</p>
            <p>리뷰 {storeInfo?.reviewCount}</p>
            <p>{storeInfo?.avgRating}</p>
            <p>{storeInfo?.bank}</p>
            <p>{storeInfo?.isBookmarked ? "♥" : "♡"}</p>
            <p onClick={handleTest}>{storeInfo?.bookmarkCount}</p>
        </div>
    )
}