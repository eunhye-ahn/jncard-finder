import { BrowserRouter, Routes, Route } from "react-router-dom"
import { MainPage } from "./pages/MainPage"
import { LoginPage } from "./pages/LoginPage"
import { SignUpPage } from "./pages/SignupPage"
import { useEffect } from "react"
import { reissue } from "./axios/api"
import { useAuthStore } from "./store/authStore"
import { StorePage } from "./pages/StorePage"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { ReactQueryDevtools } from "@tanstack/react-query-devtools"

/**
 * [tanstack query 흐름] : 서버 상태 관리 라이브러리 : 비동기데이터 
 * 
 * 1. QueryClient 생성
 * - 앱 전체의 캐시 저장소 + 기본 설정을 담당하는 두뇌
 * - staleTime: 5분 -> 5분 안에 같은 요청 오면 API 안 부르고 캐시 사용
 * - retry: 1 -> API 실패 시 1번만 재시도
 */

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,
      retry: 1,
    }
  }
})

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
    /**
     * 2. QueryClientProvider
     * - queryClient를 props로 주지않고 React Context로 앱 전체 주입
     * - 이 Provider 안에 있는 컴포넌트는 어디서든
     *    useQuery, useMutation 등 탄스택 쿼리 훅 사용 가능
     */
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/store/:storeId" element={<StorePage />} />
        </Routes>
      </BrowserRouter>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )
}

export default App

/**
 * /:storeId /뒤에 오는 모든 것을 storeId로 받겠다
 * /로 이동하니까 /:storeId가 "search"를 받아서 실행되어버림
 * 경로 하나 더두어서 해결
 */

/** ?????
*3.ReactQueryDevtools
*-개발 환경에서만 렌더됨 (프로덕션 빌드 시 자동제거)
*-현재 캐시 상태, 쿼리 키, fresh/stale/inactive 상태를 UI로 확인가능
*-initialIsOpen={false} // 처음에 닫힌 상태로 시작
*/