import { useEffect, useState } from "react"
import { getMyBookmarks } from "../../axios/api";
import type { BookmarkListResponse } from "../../type/bookmark";

export const BookmarkList = () => {
    const [bookmarkList, setBookmarkList] = useState<BookmarkListResponse[]>([]);

    useEffect(() => {
        getMyBookmarks()
            .then((res) => setBookmarkList(res.data));
    }, []);

    return (
        <div>
            {bookmarkList.length > 0 ? bookmarkList.map(b => (
                <div key={b.bookmarkId}>
                    <p>{b.storeName}</p>
                    <p>{b.category}</p>
                    <p>{b.storeName}</p>
                </div>
            )) : "북마크한 가맹점이 없습니다."}
        </div>
    )
}