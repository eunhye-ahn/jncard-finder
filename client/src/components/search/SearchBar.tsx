import { useState } from "react"

type SearchBarProps = {
    onSearch: (q: string) => void
}

export const SearchBar = ({ onSearch }: SearchBarProps) => {
    const [q, setQ] = useState("");

    return (
        <div>
            <input type="text"
                value={q}
                onChange={(e) => setQ(e.target.value)} />
            <button onClick={() => onSearch(q)}>검색</button>
        </div>
    )
}

//여기는 q만 넘겨줌