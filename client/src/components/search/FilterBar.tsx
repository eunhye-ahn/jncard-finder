import { CATEGORY_LIST, SIDO_LIST } from "../../constants/filters";
import type { SearchRequest } from "../../type/search";

type FilterBarProps = Pick<SearchRequest, "category" | "sido"> & {
    onSidoChange: (sido: SearchRequest["sido"]) => void,
    onCategoryChange: (category: SearchRequest["category"]) => void
}

export const FilterBar = ({ category, sido, onSidoChange, onCategoryChange }: FilterBarProps) => {
    return (
        <div>
            {/* 시도 */}
            <select
                value={sido ?? ""}
                onChange={(e) => onSidoChange(e.target.value || null)}
            >
                <option value="">전체</option>
                {SIDO_LIST.map(s => (
                    <option key={s} value={s}>{s}</option>
                ))}
            </select>
            {/* 카테고리 */}
            {CATEGORY_LIST.map(c => (
                <button key={c}
                    style={{ fontWeight: category === c ? "bold" : "normal" }}
                    onClick={() => onCategoryChange(c)}>
                    {c}
                </button>
            ))}
        </div>
    )
}