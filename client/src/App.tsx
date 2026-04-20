import { BrowserRouter, Routes, Route } from "react-router-dom"
import { SearchPage } from "./pages/SearchPage"
import { LoginPage } from "./pages/LoginPage"
import { SignUpPage } from "./pages/SignupPage"
import { useEffect } from "react"
import { reissue } from "./axios/api"
import { useAuthStore } from "./store/authStore"
import { StorePage } from "./pages/StorePage"

function App() {

  const { setAccessToken } = useAuthStore();

  //새로고침 (at-메모리에서 삭제 => 재발급 호출)
  useEffect(() => {
    reissue()
      .then((res) => {
        setAccessToken(res.data.accessToken)
      })
  }, []);


  return (
    <BrowserRouter>
      <Routes>
        <Route path="/search" element={<SearchPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/:storeId" element={<StorePage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
