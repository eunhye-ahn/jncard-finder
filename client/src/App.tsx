import { BrowserRouter, Routes, Route } from "react-router-dom"
import { SearchPage } from "./pages/SearchPage"

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/search" element={<SearchPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
