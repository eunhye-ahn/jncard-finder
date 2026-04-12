type SearchTopRankProps = {
    rank: string[]
}

export const SearchTopRank = ({ rank }: SearchTopRankProps) => {

    if (!rank) return;

    return (
        <div>
            <div>인기검색어 TOP10</div>
            {rank.map((r, index) => (
                <div key={r}>{index + 1}.{r}</div>
            ))}
        </div>
    )
}