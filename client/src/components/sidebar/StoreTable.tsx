import { useNavigate } from "react-router-dom"
import type { SearchResponse, Store } from "../../type/search"

type StoreTableProps = Pick<SearchResponse, "stores" | "hasNext"> & {
    onLoadMore: () => void,
    onStoreClick: (store: Store) => void
}

export const StoreTable = ({ stores, hasNext, onLoadMore, onStoreClick }: StoreTableProps) => {

    const navigate = useNavigate();

    return (
        <div>
            <table>
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>가맹점명</th>
                        <th>시도</th>
                        <th>주소</th>
                    </tr>
                </thead>
                <tbody>
                    {stores.map((store, index) =>
                        <tr key={store.storeId} onClick={() => onStoreClick(store)}>
                            <td>{index + 1}</td>
                            <td>{store.storeName}</td>
                            <td>{store.sido}</td>
                            <td>{store.address}</td>
                            <td onClick={() => navigate(`/${store.storeId}`)}>상세보기</td>
                        </tr>
                    )}
                </tbody>
            </table>
            {hasNext &&
                <button onClick={onLoadMore}>
                    더보기
                </button>
            }
        </div>
    )
}