import { useEffect, useState } from "react"
import type { SearchRequest, SearchResponse } from "../type/search";
import { searchStore } from "../axios/api";

export const SearchPage = () => {
    const [query, setQuery] = useState<SearchRequest>({
        q: null,
        sido: null,
        category: null,
        cursor: null,
        size: null
    });
    const [data, setData] = useState<SearchResponse>({
        stores: [],
        nextCursor: null,
        hasNext: false
    });

    const fetchSearch = () => {
        searchStore(query)
            .then((res) => {
                setData(res.data)
            })
    }

    useEffect(() => {
        fetchSearch();
    }, []);

    const handleSearch = () => {
        fetchSearch();
    }

    //무한스크롤 - 기존꺼 유지하고 추가
    const handleLoadMore = () => {
        searchStore({ ...query, cursor: data.nextCursor })
            .then((res) => {
                setData(prev => ({
                    ...res.data,        //nextCursor, hasNext → 새거로 교체
                    stores: [...prev.stores, ...res.data.stores] // stores → 기존 + 새거 합치기
                }))
            })
    }

    return (
        <div>
            <div>
                <input type="text"
                    onChange={(e) => setQuery(prev => ({ ...prev, q: e.target.value }))} />
                <button onClick={handleSearch}>검색</button>
            </div>
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
                        {data.stores.map((store, index) =>
                            <tr key={store.storeId}>
                                <td>{index + 1}</td>
                                <td>{store.storeName}</td>
                                <td>{store.sido}</td>
                                <td>{store.address}</td>
                            </tr>
                        )}
                    </tbody>
                </table>
                {data.hasNext &&
                    <button onClick={handleLoadMore}>
                        더보기
                    </button>
                }
            </div>
        </div>
    )
}