import { BANK_LIST, CATEGORY_LIST, SIDO_LIST } from "../../constants/filters";
import type { SearchRequest } from "../../type/search";

type FilterBarProps = Pick<SearchRequest, "category" | "sido" | "bank"> & {
    onSidoChange: (sido: SearchRequest["sido"]) => void,
    onCategoryChange: (category: SearchRequest["category"]) => void
    onBankChange: (bank: SearchRequest["bank"]) => void
}

export const FilterBar = ({ category, sido, bank, onSidoChange, onCategoryChange, onBankChange }: FilterBarProps) => {
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
            <select
                value={bank ?? ""}
                onChange={(e) => onBankChange(e.target.value || null)}>
                {BANK_LIST.map(b => (
                    <option key={b} value={b}>{b}</option>
                ))}
            </select>
            {/* 카테고리 */}
            <button
                style={{ fontWeight: category === null ? "bold" : "normal" }}
                onClick={() => onCategoryChange(null)}>
                전체
            </button>
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