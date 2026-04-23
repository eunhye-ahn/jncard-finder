import { getMyBookmarks } from "@/axios/api";
import { useQuery } from "@tanstack/react-query";

export const MyBookmarkList = () => {

    const { data, isLoading, isError } = useQuery({
        queryKey: ["myBookmars"],
        queryFn: () => getMyBookmarks().then(res => res.data)
    })

    return (
        <div>
            {(data ?? []).length > 0 ? (data ?? []).map(b => (
                isLoading ? <div>로딩 중...</div>
                    : isError ? <div>데이터를 불러오는데 실패했습니다</div>
                        : <div key={b.bookmarkId}>
                            <p>{b.storeName}</p>
                            <p>{b.category}</p>
                            <p>{b.storeName}</p>
                        </div>
            )) : "북마크한 가맹점이 없습니다."}
        </div>
    )
}