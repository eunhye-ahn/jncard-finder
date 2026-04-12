import { useEffect, useState } from "react"
import type { SearchRequest, SearchResponse } from "../type/search";
import { SearchRank, searchStore } from "../axios/api";
import { SearchBar } from "../components/search/SearchBar";
import { StoreTable } from "../components/search/StoreTable";
import { FilterBar } from "../components/search/FilterBar";
import { SearchTopRank } from "../components/search/SearchTopRank";

//상태만 들고 있기 자식들에게 prop으로 내려주기

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

    const [rank, setRank] = useState<string[]>([]);

    const fetchSearch = () => {
        searchStore(query)
            .then((res) => {
                setData(res.data)
                fetchSearchRank();
            })
    }
    const fetchSearchRank = () => {
        SearchRank()
            .then((res) => setRank(res.data))
    };

    useEffect(() => {
        fetchSearch();
    }, [query]);



    const handleSearch = (q: SearchRequest["q"]) => {
        setQuery(prev => ({ ...prev, q: q }));
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

    const handleCategoryChange = (c: SearchRequest["category"]) => {
        setQuery(prev => ({ ...prev, category: c }))
    }

    const handleSidoChange = (s: SearchRequest["sido"]) => {
        setQuery(prev => ({ ...prev, sido: s }))
    }

    return (
        <div>
            <SearchBar onSearch={handleSearch} />
            <FilterBar
                category={query.category}
                sido={query.sido}
                onCategoryChange={handleCategoryChange}
                onSidoChange={handleSidoChange} />
            <StoreTable
                stores={data.stores}
                hasNext={data.hasNext}
                onLoadMore={handleLoadMore} />
            <SearchTopRank rank={rank} />
        </div>
    )
}