import { useState } from "react"
import type { SignUpRequest } from "../type/user"
import { SignUp } from "../axios/api"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "../store/authStore"

export const SignUpPage = () => {
    const [form, setForm] = useState<SignUpRequest>({
        name: "",
        email: "",
        password: "",
        homeAddress: ""
    })
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const handleSignUp = (e: React.FormEvent) => {
        e.preventDefault()
        SignUp(form)
            .then((res) => {
                setAccessToken(res.data.accessToken)
                navigate("/search")
            })
            .catch((err) => {
                alert(err)
            })
    }

    return (
        <form onSubmit={handleSignUp}>
            <div>
                <label>이름</label>
                <input type="text"
                    onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))} />
            </div>
            <div>
                <label>이메일</label>
                <input type="email"
                    onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))} />
            </div>

            <div>
                <label>비밀번호</label>
                <input type="password"
                    onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))} />
            </div>
            <div>
                <label>주소</label>
                <input type="text"
                    onChange={(e) => setForm((prev) => ({ ...prev, homeAddress: e.target.value }))} />
            </div>
            <button>SignUp</button>
        </form>
    )
}