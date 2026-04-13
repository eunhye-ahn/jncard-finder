

type SearchTopRankProps = {
    rank: string[],
    onSearch: (q: string) => void
}

export const SearchTopRank = ({ rank, onSearch }: SearchTopRankProps) => {

    if (!rank) return;

    return (
        <div>
            <div>인기검색어 TOP10</div>
            {rank.map((r, index) => (
                <div key={r}
                    onClick={() => onSearch(r)}>
                    {index + 1}.{r}
                </div>
            ))}
        </div>
    )
}