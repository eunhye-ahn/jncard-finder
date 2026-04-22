import { useEffect, useState } from "react"
import type { SearchRequest, SearchResponse, Store } from "@/type/search";
import { Logout, SearchRank, searchStore } from "@/axios/api";
import { SearchBar } from "@/components/sidebar/SearchBar";
import { StoreTable } from "@/components/sidebar/StoreTable";
import { SearchTopRank } from "@/components/sidebar/SearchTopRank";
import { useNavigate } from "react-router-dom";

//상태만 들고 있기 자식들에게 prop으로 내려주기
type SidebarProps = {
    query: SearchRequest
    onStoreClick: (store: Store) => void
    onSearch: (q: SearchRequest["q"]) => void
}

export const Sidebar = ({ query, onStoreClick, onSearch }: SidebarProps) => {

    const [data, setData] = useState<SearchResponse>({
        stores: [],
        nextCursor: null,
        hasNext: false
    });
    const [activeTab, setActiveTab] = useState<"result" | "rank" | "my">("result")

    const [rank, setRank] = useState<string[]>([]);
    const navigate = useNavigate();

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



    const handleStoreClick = (store: Store) => {
        onStoreClick(store)
    }




    return (
        <div>
            <SearchBar onSearch={onSearch} />
            <div>
                <button onClick={() => setActiveTab("result")}>검색결과</button>
                <button onClick={() => setActiveTab("rank")}>인기순위</button>
                <button onClick={() => setActiveTab("my")}>My</button>
            </div>
            {activeTab === "result" && (
                <StoreTable
                    stores={data.stores}
                    hasNext={data.hasNext}
                    onLoadMore={handleLoadMore}
                    onStoreClick={handleStoreClick} />
            )}
            {activeTab === "rank" && (
                <SearchTopRank
                    rank={rank}
                    onSearch={onSearch} />
            )}
            {activeTab === "my" && (
                <div></div>
            )}
        </div>
    )
}