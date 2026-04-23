import { KakaoMap } from "@/components/search/KakaoMap"
import { Sidebar } from "@/components/sidebar/Sidebar"
import type { SearchRequest, Store } from "@/type/search"
import "./MainPage.css"
import { useState } from "react"
import { FilterBar } from "@/components/sidebar/FilterBar"
import { Logout } from "@/axios/api"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "@/store/authStore"


export const MainPage = () => {
    const { accessToken, clearAccessToken } = useAuthStore();
    const [selectedStore, setSelectedStore] = useState<Store | null>(null);
    const [query, setQuery] = useState<SearchRequest>({
        q: null,
        sido: null,
        category: null,
        bank: null,
        cursor: null,
        size: null
    });
    const navigate = useNavigate();

    const handleSearch = (q: SearchRequest["q"]) => {
        setQuery(prev => ({ ...prev, q: q }));
    }

    const handleCategoryChange = (c: SearchRequest["category"]) => {
        setQuery(prev => ({ ...prev, category: c }))
    }

    const handleSidoChange = (s: SearchRequest["sido"]) => {
        setQuery(prev => ({ ...prev, sido: s }))
    }
    const handleBankChange = (b: SearchRequest["bank"]) => {
        setQuery(prev => ({ ...prev, bank: b }))
    }


    const handleLogout = () => {
        Logout()
            .then(() => {
                clearAccessToken()
                navigate("/login")
            })
    }

    return (
        <div className="main-container">
            <div className="sidebar-container">
                <Sidebar query={query} onStoreClick={setSelectedStore} onSearch={handleSearch} />
            </div>
            <div className="map-container">
                <FilterBar
                    category={query.category}
                    sido={query.sido}
                    bank={query.bank}
                    onCategoryChange={handleCategoryChange}
                    onSidoChange={handleSidoChange}
                    onBankChange={handleBankChange} />
                {accessToken ? <button onClick={handleLogout}>로그아웃</button>
                    : <button onClick={() => navigate("/login")}>로그인</button>}
                <KakaoMap selectedStore={selectedStore} />
            </div>
        </div>
    )
}