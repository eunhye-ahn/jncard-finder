import { BrowserRouter, Routes, Route } from "react-router-dom"
import { SearchPage } from "./pages/SearchPage"
import { LoginPage } from "./pages/LoginPage"
import { SignUpPage } from "./pages/SignupPage"

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/search" element={<SearchPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
