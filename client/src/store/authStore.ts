import { create } from "zustand";

interface AUthStore {
    accessToken: string | null,
    setAccessToken: (token: string | null) => void,
    clearAccessToken: () => void
}

export const useAuthStore = create<AUthStore>(
    (set) => ({
        accessToken: null,
        setAccessToken: (token) => set({ accessToken: token }),
        clearAccessToken: () => set({ accessToken: null })
    })
);
