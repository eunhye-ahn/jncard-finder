import { useEffect, useState } from "react"
import { getStoreDetail } from "../axios/api";
import type { StoreDetailResponse } from "../type/search";
import { useParams } from "react-router-dom";
import { StoreReview } from "../components/review/StoreReview";

export const StorePage = () => {
    const { storeId } = useParams();
    const [storeInfo, setStoreInfo] = useState<StoreDetailResponse>({} as StoreDetailResponse);
    useEffect(() => {
        getStoreDetail(Number(storeId))
            .then((res) => {
                setStoreInfo(res.data);
                console.log(res.data)
            })
    }, []);

    return (
        <div>
            <div>
                <p>{storeInfo.storeName}</p>
                <p>{storeInfo.category}</p>
                <p>{storeInfo.avgRating}</p>
                <p>({storeInfo.reviewCount})</p>
                <p>{storeInfo.address}</p>
            </div>
            <div>
                <StoreReview storeId={storeInfo.storeId} />
            </div>
        </div>
    )
}