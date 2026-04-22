import { useRef, useState } from "react"
import { SearchAutocomplete } from "../../axios/api";

type SearchBarProps = {
    onSearch: (q: string) => void
}

export const SearchBar = ({ onSearch }: SearchBarProps) => {
    const [q, setQ] = useState("");
    const [suggestions, setSuggestions] = useState<string[]>([])
    const timerRef = useRef<ReturnType<typeof setTimeout>>(0)

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value
        setQ(value)

        clearTimeout(timerRef.current)

        timerRef.current = setTimeout(() => {
            if (value) {
                SearchAutocomplete(value)
                    .then((res) => setSuggestions(res.data))
            }
            else {
                setSuggestions([]);
            }
        }, 300)
    }

    return (
        <div>
            <input type="text"
                value={q}
                onChange={handleChange} />
            <button onClick={() => {
                onSearch(q)
                setSuggestions([]);
            }}>검색</button>
            {suggestions.length > 0 && (
                <div>
                    {suggestions.map(s => (
                        <div key={s}
                            onClick={() => {
                                onSearch(s)
                                setSuggestions([]);
                            }}>
                            {s}
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}