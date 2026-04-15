import { useEffect, useState } from "react"
import type { Store, StoreDetailResponse } from "../../type/search"
import { getStoreDetail } from "../../axios/api";

interface StoreDetailProp {
    selectedStoreId: number,

}

export const StoreDetail = ({ selectedStoreId }: StoreDetailProp) => {
    const [storeInfo, setStoreInfo] = useState<StoreDetailResponse>()

    useEffect(() => {
        getStoreDetail(selectedStoreId)
            .then((res) => {
                setStoreInfo(res.data)
            })
    }, [selectedStoreId]);

    return (
        <div>
            <div>상세보기 화면</div>
            <p>{storeInfo?.storeName}</p>
            <p>{storeInfo?.category}</p>
            <p>리뷰 {storeInfo?.reviewCount}</p>
            <p>{storeInfo?.avgRating}</p>
            <p>{storeInfo?.bank}</p>
        </div>
    )
}