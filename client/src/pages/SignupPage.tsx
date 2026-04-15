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
        homeAddress: null
    })
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();
    const [passwordError, setPasswordError] = useState("");
    const [passwordFocus, setPasswordFocus] = useState(false);
    const [passwordCheck, setPasswordCheck] = useState("");
    const [passwordChekFocus, setPasswordCheckFocus] = useState(false);


    const handleSignUp = (e: React.FormEvent) => {
        e.preventDefault()
        SignUp(form)
            .then((res) => {
                setAccessToken(res.data.accessToken)
                navigate("/search")
            })
            .catch(() => {
                setForm({
                    name: "",
                    email: "",
                    password: "",
                    homeAddress: null
                })
                setPasswordError("")
                setPasswordCheck("")
            })
    }

    const validatePassword = (value: string) => {
        if (value.length < 8) {
            setPasswordError("8자이상 입력하세요")
        }
        else if (!/(?=.*[A-Za-z])(?=.*\d)/.test(value)) {
            setPasswordError("영문+숫자 조합이어야 합니다")
        } else {
            setPasswordError("") //통과
        }
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
                <div>
                    <label>비밀번호</label>
                    <input type="password"
                        onFocus={() => setPasswordFocus(true)}
                        onBlur={() => setPasswordFocus(false)}
                        onChange={(e) => {
                            setForm((prev) => ({ ...prev, password: e.target.value }))
                            validatePassword(e.target.value)
                        }} />
                    {passwordFocus && passwordError && <p>{passwordError}</p>}
                </div>
                <div>
                    <label>비밀번호 확인</label>
                    <input type="password"
                        onFocus={() => setPasswordCheckFocus(true)}
                        onBlur={() => setPasswordCheckFocus(false)}
                        onChange={(e) => {
                            setPasswordCheck(e.target.value)
                        }}
                    />
                    {passwordChekFocus && passwordCheck && passwordCheck != form.password && <p>
                        비밀번호가 일치하지않습니다
                    </p>}
                </div>
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