import { useNavigate } from "react-router-dom"
import type { SearchResponse, Store } from "../../type/search"
import { useEffect, useRef } from "react"
import '@/components/sidebar/StoreTable.css'

type StoreTableProps = Pick<SearchResponse, "stores" | "hasNext"> & {
    onLoadMore: () => void,
    onStoreClick: (store: Store) => void,
    isSearchLoading: boolean
}

export const StoreTable = ({ stores, hasNext, isSearchLoading, onLoadMore, onStoreClick }: StoreTableProps) => {
    const sentinel = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!sentinel.current) return;

        //보이면 onLeadMore 호출 하는 함수
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && hasNext) {
                    onLoadMore();
                }
            },
            { threshold: 0.1 } //10%보이면
        );

        //ref div에 함수호출
        observer.observe(sentinel.current);
        //컴포넌트 언마운트 시, 감시 해제 - 메모리 누수 방지
        return () => observer.disconnect();
    }, [hasNext, onLoadMore]);

    return (
        <div className="store-list">
            {stores.length > 0 ? stores.map((store, index) =>
                <div key={store.storeId} className="store-list-item" onClick={() => onStoreClick(store)}>
                    <div className="store-list-item-header">
                        <span className="store-list-item-index">{index + 1}</span>
                        <span className="store-list-item-name">{store.storeName}</span>
                    </div>
                    <p className="store-list-item-sido">{store.sido}</p>
                    <p className="store-list-item-address">{store.address}</p>
                    <button className="store-list-item-detail" onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/store/${store.storeId}`)
                    }}>상세보기</button>
                </div>
            )
                : <div className="store-list-empty">검색결과가 없습니다</div>
            }
            <div ref={sentinel} style={{ height: '1px' }} />
            {isSearchLoading && <div className="store-list-loading">로딩중...</div>}
        </div>
    )
}