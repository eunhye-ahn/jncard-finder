import { useEffect, useState } from "react"
import type { SearchRequest, SearchResponse, Store } from "@/type/search";
import { Logout, SearchRank, searchStore } from "@/axios/api";
import { SearchBar } from "@/components/sidebar/SearchBar";
import { StoreTable } from "@/components/sidebar/StoreTable";
import { SearchTopRank } from "@/components/sidebar/SearchTopRank";
import { useNavigate } from "react-router-dom";
import { useQuery, useInfiniteQuery } from "@tanstack/react-query"
import { MyReviewList } from "../review/MyReviewList";
import { MyBookmarkList } from "../bookmark/MyBookmarkList";

//상태만 들고 있기 자식들에게 prop으로 내려주기
//useQuery-탄스택쿼리가 제공하는 훅, 어떤 데이터 가져올지 알려주면 로딩/에러/데이터 상태관리
type SidebarProps = {
    query: SearchRequest
    onStoreClick: (store: Store) => void
    onSearch: (q: SearchRequest["q"]) => void
}

export const Sidebar = ({ query, onStoreClick, onSearch }: SidebarProps) => {

    // const [data, setData] = useState<SearchResponse>({
    //     stores: [],
    //     nextCursor: null,
    //     hasNext: false
    // });
    const {
        data: searchData,
        isLoading: isSearchLoading,
        isError: isSearchError,
        fetchNextPage,
        hasNextPage
    } = useInfiniteQuery({
        queryKey: ["search", query], //query바뀔때 실행
        queryFn: ({ pageParam }: { pageParam: string | null }) => searchStore({ ...query, cursor: pageParam }).then(res => res.data),
        //에러나면 → isSearchError가 true가 됨
        initialPageParam: null,
        getNextPageParam: (lastPage: SearchResponse) => lastPage.hasNext ? lastPage.nextCursor : undefined,
    });

    const { data: rankData, isLoading: isRankLoading, isError: isRankError } = useQuery({
        queryKey: ["searchRank"], //마운트될때 실행
        queryFn: () => SearchRank().then(res => res.data)
    });


    const [activeTab, setActiveTab] = useState<"result" | "rank" | "my">("result")
    const navigate = useNavigate();



    //무한스크롤 - 기존꺼 유지하고 추가
    // const handleLoadMore = () => {
    //     searchStore({ ...query, cursor: searchData.nextCursor })
    //         .then((res) => {
    //             setData(prev => ({
    //                 ...res.data,        //nextCursor, hasNext → 새거로 교체
    //                 stores: [...prev.stores, ...res.data.stores] // stores → 기존 + 새거 합치기
    //             }))
    //         })
    // }

    return (
        <div>
            <SearchBar onSearch={onSearch} />
            <div>
                <button onClick={() => setActiveTab("result")}>검색결과</button>
                <button onClick={() => setActiveTab("rank")}>인기순위</button>
                <button onClick={() => setActiveTab("my")}>My</button>
            </div>
            {activeTab === "result" && (
                isSearchLoading ? <div>로딩중...</div>
                    : isSearchError ? <div>검색에 실패했습니다</div>
                        : <StoreTable
                            stores={searchData?.pages.flatMap(page => page.stores) ?? []}
                            hasNext={hasNextPage ?? false}
                            onLoadMore={fetchNextPage}
                            onStoreClick={onStoreClick} />
            )}
            {activeTab === "rank" && (
                isRankLoading ? <div>로딩중...</div>
                    : isRankError ? <div>인기순위를 불러오는 데 실패했습니다</div>
                        : <SearchTopRank
                            rank={rankData ?? []}
                            onSearch={onSearch} />
            )}
            {activeTab === "my" && (
                <div>
                    <MyBookmarkList />
                    <MyReviewList />
                </div>
            )}
        </div>
    )
}