import { useState } from "react"
import { SearchAutocomplete } from "../../axios/api";

type SearchBarProps = {
    onSearch: (q: string) => void
}

export const SearchBar = ({ onSearch }: SearchBarProps) => {
    const [q, setQ] = useState("");
    const [suggestions, setSuggestions] = useState<string[]>([])

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setQ(e.target.value)
        SearchAutocomplete(e.target.value)
            .then((res) => setSuggestions(res.data))
    }

    return (
        <div>
            <input type="text"
                value={q}
                onChange={handleChange} />
            <button onClick={() => onSearch(q)}>검색</button>
            {suggestions.length > 0 && (
                <div>
                    {suggestions.map(s => (
                        <div key={s}>
                            {s}
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}

//여기는 q만 넘겨줌